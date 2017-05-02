/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.sys.datatools.liquirelational;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.datatools.util.PropertyLoadingFactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class LiquiRelational {
    private static final Logger LOG = Logger.getLogger(LiquiRelational.class);

    protected static final String UPDATE_DATABASE_FULL_REBUILD = "updateDatabaseFullRebuild";
    protected static final String UPDATE_DATABASE_MANUAL_START = "updateDatabaseManualStart";
    protected static final String UPDATE_DATABASE_MANUAL_END = "updateDatabaseManualEnd";
    protected static final String UPDATE_DATABASE_CONTEXT = "updateDatabaseContext";
    protected static final String UPDATE_DATABASE_PACKAGES = "updateDatabasePackages";
    protected static final String UPDATE_DATABASE_PACKAGES_RICE = "updateDatabasePackagesRice";

    private ClassPathXmlApplicationContext applicationContext;
    protected Properties properties = null;

    public static void main(String[] args) {
        BasicConfigurator.configure();

        LiquiRelational liquiRelational = new LiquiRelational();
        liquiRelational.updateDatabase();
        System.exit(0);
    }

    public LiquiRelational() {
    }

    public LiquiRelational(Properties properties) {
        this.properties = properties;
    }

    public void updateDatabase() {
        initializeContext();
        applyUpdates();
        applicationContext.close();
    }

    private void initializeContext() {
        long startInit = System.currentTimeMillis();
        if (LOG.isInfoEnabled()) {
            LOG.info("Initializing LiquiRelational Context...");
        }

        applicationContext = new ClassPathXmlApplicationContext("org/kuali/kfs/sys/datatools/liquirelational/kfs-liqui-relational-bootstrap.xml");
        if (this.properties != null) {
            applicationContext.getEnvironment().getPropertySources().addFirst(new PropertiesSource("properties", this.properties));
        } else {
            this.properties = applicationContext.getBean("properties", Properties.class);
        }
        applicationContext.start();

        long endInit = System.currentTimeMillis();
        if (LOG.isInfoEnabled()) {
            LOG.info("...LiquiRelational Context successfully initialized, startup took " + (endInit - startInit) + " ms.");
        }
    }

    private void applyUpdates() {
        applyDatabaseUpdates("dataSource", UPDATE_DATABASE_PACKAGES);
        applyDatabaseUpdates("riceDataSource", UPDATE_DATABASE_PACKAGES_RICE);
    }

    private void applyDatabaseUpdates(String dataSource, String databaseUpdatePackages) {
        DataSource kfsDataSource = applicationContext.getBean(dataSource, DataSource.class);
        List<String> packages = getBaseListProperty(databaseUpdatePackages);
        if (isEmptyList(packages)) {
            LOG.info(databaseUpdatePackages + " property is empty, nothing to update.");
        } else {
            updateDatabase(kfsDataSource, packages);
        }
    }

    private boolean isEmptyList(List<String> packages) {
        if (CollectionUtils.isEmpty(packages)) {
            return true;
        }

        for (String pkg : packages) {
            if (StringUtils.isNotBlank(pkg)) {
                return false;
            }
        }

        return true;
    }

    private void updateDatabase(DataSource dataSource, List<String> packages) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(applicationContext.getClassLoader());
            String liquibaseContext = getBaseProperty(UPDATE_DATABASE_CONTEXT);

            runUpdatesPhase(database, resourceAccessor, liquibaseContext, packages);
        } catch (SQLException | DatabaseException e) {
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

    private List<String> getPhasesToRun(List<String> packages) {
        List<String> phases = getManualPhasesToRun(packages);
        if (phases == null) {
            phases = getAutoPhasesToRun(packages);
        }
        return phases;
    }

    private List<String> getManualPhasesToRun(List<String> packages) {
        String start = getBaseProperty(UPDATE_DATABASE_MANUAL_START);
        String end = getBaseProperty(UPDATE_DATABASE_MANUAL_END);
        if (start == null) {
            return null;
        }

        int startNumber = 0;
        int endNumber = 0;
        try {
            startNumber = Integer.parseInt(start);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid parameter: " + UPDATE_DATABASE_MANUAL_START);
        }

        try {
            endNumber = Integer.parseInt(end);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid parameter: " + UPDATE_DATABASE_MANUAL_END);
        }

        if (startNumber < 1 || startNumber > 5) {
            throw new RuntimeException("Invalid parameter: " + UPDATE_DATABASE_MANUAL_START);
        }
        if (endNumber < 1 || endNumber > 5) {
            throw new RuntimeException("Invalid parameter: " + UPDATE_DATABASE_MANUAL_END);
        }

        LOG.info("getManualPhasesToRun() running phase " + startNumber + " to " + endNumber);
        List<String> phaseFilenames = new ArrayList<>();
        for (int i = startNumber; i <= endNumber; i++) {
            phaseFilenames.addAll(findFilenamesForPhase(i, packages));
        }
        return phaseFilenames;
    }

    private List<String> getAutoPhasesToRun(List<String> packages) {
        String updateDatabaseFullRebuild = getBaseProperty(UPDATE_DATABASE_FULL_REBUILD);

        List<String> phaseFilenames = new ArrayList<>();
        if (Boolean.parseBoolean(updateDatabaseFullRebuild)) {
            LOG.info("getAutoPhasesToRun() Running all phases");
            for (int i = 1; i < 5; i++) {
                phaseFilenames.addAll(findFilenamesForPhase(i, packages));
            }
        } else {
            LOG.info("getAutoPhasesToRun() Running phase 5 only");
        }

        phaseFilenames.addAll(findFilenamesForPhase(5, packages));
        return phaseFilenames;
    }

    private void runUpdatesPhase(Database database, ResourceAccessor resourceAccessor, String liquibaseContext, List<String> packages) {
        List<String> phaseFilenames = getPhasesToRun(packages);

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

    private List<String> findFilenamesForPhase(int phase, List<String> packages) {
        List<String> phaseFilenames = new ArrayList<>();

        for (String pkg : packages) {
            if (StringUtils.isEmpty(pkg)) {
                LOG.info("Package is empty, no files to find.");
            } else {
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
        }
        return phaseFilenames;
    }

    public String getBaseProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public List<String> getBaseListProperty(String propertyName) {
        if (properties.containsKey(propertyName)) {
            return Arrays.asList(properties.getProperty(propertyName).split(","));
        } else {
            return Collections.emptyList();
        }
    }

    public static class PropertiesSource extends PropertySource<String> {
        protected Properties properties;

        public PropertiesSource(String name, Properties properties) {
            super(name);
            this.properties = properties;
        }

        @Override
        public String getProperty(String s) {
            if (properties != null) {
                return String.valueOf(properties.get(s));
            } else {
                return null;
            }
        }
    }
}
