/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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

import java.applet.AppletContext;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.datadictionary.DataDictionaryException;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.util.ResourceLoaderUtil;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.impex.xml.DirectoryXmlDocCollection;
import org.kuali.rice.core.api.impex.xml.FileXmlDocCollection;
import org.kuali.rice.core.api.impex.xml.XmlDocCollection;
import org.kuali.rice.core.api.impex.xml.XmlIngesterService;
import org.kuali.rice.core.api.impex.xml.ZipXmlDocCollection;
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl;
import org.kuali.rice.kew.batch.XmlPollerServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;

public class WorkflowImporter {
    private static final String WORKFLOW_DIRECTORY = "workflow.directory";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowImporter.class);
    private static final String PENDING_MOVE_FAILED_ARCHIVE_FILE = "movesfailed";

    private static ClassPathXmlApplicationContext context;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("ERROR: You must pass the base directory on the command line.");
            System.exit(-1);
        }
        initializeKfs();
        importWorkflow(context, args[0]);
    }

    public static void initializeKfs() {
        long startInit = System.currentTimeMillis();
        LOG.info("Initializing Kuali Rice Application...");

        String bootstrapSpringBeans = "kfs-startup.xml";
//        String bootstrapSpringBeans = "kfs-workflow-importer-startup.xml";

        Properties baseProps = new Properties();
        baseProps.putAll(System.getProperties());
        JAXBConfigImpl config = new JAXBConfigImpl(baseProps);
        ConfigContext.init(config);

        context = new ClassPathXmlApplicationContext(bootstrapSpringBeans);

        context.start();
        long endInit = System.currentTimeMillis();
        LOG.info("...Kuali Rice Application successfully initialized, startup took " + (endInit - startInit) + " ms.");
    }

    public static void importWorkflow(ApplicationContext applicationContext) {
        String xmlDir = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(WORKFLOW_DIRECTORY);
        if (StringUtils.isBlank(xmlDir)) {
            LOG.info(WORKFLOW_DIRECTORY +" was blank; will not import workflow");
            return;
        }

        importWorkflow(applicationContext, xmlDir);
    }

    protected static void importWorkflow(ApplicationContext applicationContext, String xmlDir) {
        List<XmlDocCollection> collections = new ArrayList<>();

        for (String file : Arrays.asList(xmlDir.split(","))) {
            String trimmedFile = file.trim();
            if (!trimmedFile.isEmpty()) {
                if (trimmedFile.endsWith(".xml")) {
                    try {
                        for (Resource resource : ResourcePatternUtils.getResourcePatternResolver(applicationContext).getResources(trimmedFile)) {
                            if (resource.exists()) {
                                final String resourcePath = ResourceLoaderUtil.parseResourcePathFromUrl(resource);
                                if (!StringUtils.isBlank(resourcePath)) {
                                    collections.add(new FileXmlDocCollection(resource.getFile()));
                                }
                            } else {
                                LOG.warn("Could not find " + xmlDir);
                                throw new DataDictionaryException(WORKFLOW_DIRECTORY + " " + resource.getFilename() + " not found");
                            }
                        }
                    } catch (IOException e) {
                        LOG.error("Error loading resource: " + trimmedFile);
                    }
                } else if (trimmedFile.endsWith(".zip")) {
                    try {
                        collections.add(new ZipXmlDocCollection(new File(trimmedFile)));
                    } catch (IOException ioe) {
                        LOG.error("Unable to load file: " + trimmedFile);
                    }
                }
            }
        }

        LOG.info("Found " + collections.size() + " files to ingest.");

        XmlIngesterService xmlIngesterService = CoreApiServiceLocator.getXmlIngesterService();
        try {
            xmlIngesterService.ingest(collections);
        } catch (Exception e) {
            LOG.error("Well something went wrong, hopefully there are some error messages", e);
        }
    }
}
