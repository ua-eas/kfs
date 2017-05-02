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
package org.kuali.kfs.sys.datatools.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class PropertyLoadingFactoryBean implements FactoryBean<Properties> {
    private static final Properties BASE_PROPERTIES = new Properties();
    private static final String FINANCIALS_DEFAULT_CONFIG = "/org/kuali/kfs/sys/datatools/financials-default-config";
    private static final String FINANCIALS_SECURITY_DEFAULT_CONFIG = "/org/kuali/kfs/sys/datatools/financials-security-default-config";
    private static final String SECURITY_PROPERTY_FILE_NAME_KEY = "security.property.file";
    private static final String ADDITIONAL_FINANCIALS_CONFIG_LOCATIONS_PARAM = "additional.kfs.config.locations";
    private Properties props = new Properties();

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PropertyLoadingFactoryBean.class);

    public Properties getObject() {
        loadBaseProperties();
        props.putAll(BASE_PROPERTIES);
        loadPropertyList(props, SECURITY_PROPERTY_FILE_NAME_KEY);

        if (LOG.isDebugEnabled()) {
            for (Object key : props.keySet()) {
                String value = (String) props.get(key);
                LOG.debug(key + ": " + value);
            }
        }
        return props;
    }

    public Class<Properties> getObjectType() {
        return Properties.class;
    }

    public boolean isSingleton() {
        return true;
    }

    private static void loadPropertyList(Properties props, String listPropertyName) {
        for (String propertyFileName : getBaseListProperty(listPropertyName)) {
            loadProperties(props, propertyFileName);
        }
    }

    private static void loadProperties(Properties props, String propertyFileName) {
        InputStream propertyFileInputStream = null;
        try {
            try {
                propertyFileInputStream = new DefaultResourceLoader(getDefaultClassLoader()).getResource(propertyFileName).getInputStream();
                props.load(propertyFileInputStream);
            } finally {
                if (propertyFileInputStream != null) {
                    propertyFileInputStream.close();
                }
            }
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public static String getBaseProperty(String propertyName) {
        loadBaseProperties();
        return BASE_PROPERTIES.getProperty(propertyName);
    }

    public static List<String> getBaseListProperty(String propertyName) {
        loadBaseProperties();
        if (BASE_PROPERTIES.containsKey(propertyName)) {
            return Arrays.asList(BASE_PROPERTIES.getProperty(propertyName).split(","));
        } else {
            return Collections.emptyList();
        }
    }

    protected static void loadBaseProperties() {
        if (BASE_PROPERTIES.isEmpty()) {
            loadProperties(BASE_PROPERTIES, new StringBuilder("classpath:").append(FINANCIALS_DEFAULT_CONFIG).append(".properties").toString());
            loadProperties(BASE_PROPERTIES, new StringBuilder("classpath:").append(FINANCIALS_SECURITY_DEFAULT_CONFIG).append(".properties").toString());
            loadExternalProperties(BASE_PROPERTIES, ADDITIONAL_FINANCIALS_CONFIG_LOCATIONS_PARAM);
        }
    }

    /**
     * Loads properties from an external file.  Also merges in all System properties
     *
     * @param props the properties object
     */
    private static void loadExternalProperties(Properties props, String location) {
        String externalConfigLocationPaths = System.getProperty(location);
        if (StringUtils.isNotEmpty(externalConfigLocationPaths)) {
            String[] files = externalConfigLocationPaths.split(",");
            for (String f : files) {
                if (StringUtils.isNotEmpty(f)) {
                    LOG.info("Loading properties from " + f);
                    loadProperties(props, new StringBuffer("file:").append(f).toString());
                }
            }
        }

        props.putAll(System.getProperties());
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = PropertyLoadingFactoryBean.class.getClassLoader();
        }
        return classLoader;
    }
}
