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
