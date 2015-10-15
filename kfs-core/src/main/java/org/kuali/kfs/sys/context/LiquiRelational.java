/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2015 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.context;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class LiquiRelational {
    private static final Logger LOG = Logger.getLogger(LiquiRelational.class);

    protected static final String DATABASE_UPDATE_FULL_REBUILD = "databaseUpdateFullRebuild";
    protected static final String DATABASE_UPDATE_CONTEXT = "databaseUpdateContext";
    protected static final String DATABASE_UPDATE_PACKAGES = "databaseUpdatePackages";

    private ClassPathXmlApplicationContext applicationContext;

    public static void main(String[] args) {
        LiquiRelational liquiRelational = new LiquiRelational();
        liquiRelational.updateDatabase();
        System.exit(0);
    }

    public void updateDatabase() {
        initializeContext();
        applyUpdates();
        applicationContext.close();
    }

    private void initializeContext() {
        long startInit = System.currentTimeMillis();
        LOG.info("Initializing LiquiRelational Context...");

        String bootstrapSpringBeans = "kfs-liqui-relational-startup.xml";

        Properties baseProps = new Properties();
        baseProps.putAll(System.getProperties());
        JAXBConfigImpl config = new JAXBConfigImpl(baseProps);
        ConfigContext.init(config);

        applicationContext = new ClassPathXmlApplicationContext(bootstrapSpringBeans);

        applicationContext.start();

        SpringContext.applicationContext = applicationContext;

        long endInit = System.currentTimeMillis();
        LOG.info("...LiquiRelational Context successfully initialized, startup took " + (endInit - startInit) + " ms.");
    }

    private void applyUpdates() {
        DataSource dataSource = SpringContext.getBean(DataSource.class);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(ClassLoaderUtils.getDefaultClassLoader());
            String liquibaseContext = PropertyLoadingFactoryBean.getBaseProperty(DATABASE_UPDATE_CONTEXT);

            runUpdatesPhase(database, resourceAccessor, liquibaseContext);
        } catch (SQLException|DatabaseException e) {
            LOG.error("Failed to get datasource.", e);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOG.warn("Failed to get close connection.", e);
                    connection = null;
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void runUpdatesPhase(Database database, ResourceAccessor resourceAccessor, String liquibaseContext) {
        List<String> phaseFilenames = new ArrayList<>();
        if (Boolean.parseBoolean(PropertyLoadingFactoryBean.getBaseProperty(DATABASE_UPDATE_FULL_REBUILD))) {
            for (int i=1; i<5;i++) {
                phaseFilenames.addAll(findFilenamesForPhase(i));
            }
        }

        phaseFilenames.addAll(findFilenamesForPhase(5));

        for (String filename : phaseFilenames) {
            try {
                LOG.info("Processing " + filename);
                Liquibase liquibase = new Liquibase(filename, resourceAccessor, database);
                liquibase.update(liquibaseContext);
            } catch (LiquibaseException e) {
                throw new RuntimeException("Failed to create Liquibase for " + filename, e);
            }
        }
    }

    private List<String> findFilenamesForPhase(int phase) {
        List<String> phaseFilenames = new ArrayList<>();

        for (String pkg : PropertyLoadingFactoryBean.getBaseListProperty(DATABASE_UPDATE_PACKAGES)) {
            String sourceName = "classpath:/" + pkg + "/db/phase" + phase + "/*.xml";
            try {
                List<String> tempFilenames = new ArrayList<>();
                final Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(applicationContext).getResources(sourceName);
                for (int i = 0; i < resources.length; i++) {
                    tempFilenames.add(pkg + "/db/phase" + phase + "/" + resources[i].getFilename());
                }
                Collections.sort(tempFilenames);
                phaseFilenames.addAll(tempFilenames);
            } catch (FileNotFoundException e) {
                LOG.warn("Failed to find files for " + sourceName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return phaseFilenames;
    }

}
