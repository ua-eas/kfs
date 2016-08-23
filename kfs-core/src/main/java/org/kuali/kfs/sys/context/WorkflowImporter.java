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
package org.kuali.kfs.sys.context;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.DataDictionaryException;
import org.kuali.kfs.krad.util.ResourceLoaderUtil;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.rule.service.RuleAttributeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class WorkflowImporter {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowImporter.class);

    private static final String WORKFLOW_PATH = "workflow.path";
    private static ClassPathXmlApplicationContext context;

    public static void main(String[] args) {
        initializeKfs();
        new WorkflowImporter().importWorkflow(context);
        System.exit(0);
    }

    public static void initializeKfs() {
        long startInit = System.currentTimeMillis();
        LOG.info("Initializing Kuali Rice Application...");

        String bootstrapSpringBeans = "kfs-workflow-importer-startup.xml";

        Properties baseProps = new Properties();
        baseProps.putAll(System.getProperties());
        JAXBConfigImpl config = new JAXBConfigImpl(baseProps);
        ConfigContext.init(config);

        context = new ClassPathXmlApplicationContext(bootstrapSpringBeans);
        context.start();

        SpringContext.applicationContext = context;

        long endInit = System.currentTimeMillis();
        LOG.info("...Kuali Rice Application successfully initialized, startup took " + (endInit - startInit) + " ms.");
    }

    public void importWorkflow(ApplicationContext applicationContext) {
        String xmlDir = PropertyLoadingFactoryBean.getBaseProperty(WORKFLOW_PATH);
        if (StringUtils.isBlank(xmlDir)) {
            LOG.info(WORKFLOW_PATH + " was blank; will not import workflow");
            return;
        }

        DocumentTypeService documentTypeService = KEWServiceLocator.getDocumentTypeService();
        RuleAttributeService ruleAttributeService = KEWServiceLocator.getRuleAttributeService();

        for (String file : Arrays.asList(xmlDir.split(","))) {
            String trimmedFile = file.trim();
            if (!trimmedFile.isEmpty()) {
                if (trimmedFile.endsWith(".xml")) {
                    try {
                        for (Resource resource : ResourcePatternUtils.getResourcePatternResolver(applicationContext).getResources(trimmedFile)) {
                            if (resource.exists()) {
                                final String resourcePath = ResourceLoaderUtil.parseResourcePathFromUrl(resource);
                                if (!StringUtils.isBlank(resourcePath)) {
                                    LOG.info("Attempting to ingest: " + resourcePath);
                                    ruleAttributeService.loadXml(resource.getInputStream(), "KFS");
                                    documentTypeService.loadXml(resource.getInputStream(), "KFS");
                                }
                            } else {
                                LOG.warn("Could not find " + xmlDir);
                                throw new DataDictionaryException(WORKFLOW_PATH + " " + resource.getFilename() + " not found");
                            }
                        }
                    } catch (IOException e) {
                        LOG.error("Error loading resource: " + trimmedFile);
                        e.printStackTrace();
                    }
                } else if (trimmedFile.endsWith(".zip")) {
                    try {
                        ruleAttributeService.loadXml(new FileInputStream(new File(trimmedFile)), "KFS");
                        documentTypeService.loadXml(new FileInputStream(new File(trimmedFile)), "KFS");
                    } catch (IOException ioe) {
                        LOG.error("Unable to load file: " + trimmedFile);
                    }
                }
            }
        }
    }
}
