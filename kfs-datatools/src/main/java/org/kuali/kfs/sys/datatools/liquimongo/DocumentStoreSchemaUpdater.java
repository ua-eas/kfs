/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.sys.datatools.liquimongo;

import org.kuali.kfs.sys.datatools.liquimongo.service.DocumentStoreSchemaUpdateService;
import org.apache.log4j.BasicConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DocumentStoreSchemaUpdater {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentStoreSchemaUpdater.class);
    private static ClassPathXmlApplicationContext context;

    public static void main(String[] args) {
        BasicConfigurator.configure();

        if (args.length < 1) {
            LOG.error("You must provide a path to the migration files.");
        } else if (!args[0].endsWith(".json")) {
            LOG.error("Path needs to point to a json file.");
        } else {
            initializeSpring();
            start(args[0]);
        }
    }

    private static void start(String updateFilePath) {
        DocumentStoreSchemaUpdateService service = context.getBean(DocumentStoreSchemaUpdateService.class);
        service.updateDocumentStoreSchemaForLocation(updateFilePath);
    }

    public static void initializeSpring() {
        long startInit = System.currentTimeMillis();
        if (LOG.isInfoEnabled()) {
            LOG.info("Initializing Spring Context...");
        }

        context = new ClassPathXmlApplicationContext("org/kuali/kfs/sys/datatools/liquimongo/spring-liquimongo-bootstrap.xml", "org/kuali/kfs/sys/datatools/liquimongo/spring-liquimongo.xml");
        context.start();

        if (LOG.isInfoEnabled()) {
            long endInit = System.currentTimeMillis();
            LOG.info("...Spring Context successfully initialized, startup took " + (endInit - startInit) + " ms.");
        }
    }
}
