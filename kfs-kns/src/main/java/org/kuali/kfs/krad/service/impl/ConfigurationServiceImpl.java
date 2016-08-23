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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.Truth;
import org.kuali.kfs.kns.web.struts.action.KualiPropertyMessageResources;
import org.kuali.kfs.kns.web.struts.action.KualiPropertyMessageResourcesFactory;
import org.kuali.kfs.krad.exception.DuplicateKeyException;
import org.kuali.kfs.krad.exception.PropertiesException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * ConfigurationService implementation which is a thin wrapper over the core Config object, and which
 * merges in Kuali message resource properties.
 */
public class ConfigurationServiceImpl implements ConfigurationService {
    private final PropertyHolder propertyHolder = new PropertyHolder();

    /**
     * Harcoding the configFileName, by request.
     */
    public ConfigurationServiceImpl() {
        this.propertyHolder.getHeldProperties().putAll(ConfigContext.getCurrentContextConfig().getProperties());

        KualiPropertyMessageResourcesFactory propertyMessageFactory = new KualiPropertyMessageResourcesFactory();

        // create default KualiPropertyMessageResources
        KualiPropertyMessageResources messageResources =
                (KualiPropertyMessageResources) propertyMessageFactory.createResources("");

        //Add Kuali Properties to property holder
        this.propertyHolder.getHeldProperties().putAll(messageResources.getKualiProperties(null));
    }

    /**
     * @see org.kuali.rice.core.api.config.property.ConfigurationService#getPropertyValueAsString(java.lang.String)
     */
    @Override
    public String getPropertyValueAsString(String key) {
        if (key == null) {
            throw new IllegalArgumentException("invalid (null) key");
        }

        return this.propertyHolder.getProperty(key);
    }

    /**
     * @see org.kuali.rice.core.api.config.property.ConfigurationService#getPropertyValueAsBoolean(java.lang.String)
     */
    @Override
    public boolean getPropertyValueAsBoolean(String key) {
        if (key == null) {
            throw new IllegalArgumentException("invalid (null) key");
        }
        String property = this.propertyHolder.getProperty(key);
        Boolean b = Truth.strToBooleanIgnoreCase(property);
        if (b == null) {
            return false;
        }
        return b;
    }

    /**
     * @see org.kuali.rice.core.api.config.property.ConfigurationService#getAllProperties()
     */
    @Override
    public Map<String, String> getAllProperties() {
        return (Map) Collections.unmodifiableMap(propertyHolder.getHeldProperties());
    }

    /**
     * This is an interface for a source for properties
     *
     *
     */
    static interface PropertySource {
        /**
         * @return Properties loaded from this PropertySource
         * @throws PropertiesException if there's a problem loading the properties
         */
        public Properties loadProperties();
    }

    /**
     * This class is a Property container. It is able to load properties from various property-sources.
     *
     *
     */
    static class PropertyHolder {
        private static Logger LOG = Logger.getLogger(PropertyHolder.class);

        Properties heldProperties;

        /**
         * Default constructor.
         */
        public PropertyHolder() {
            this.heldProperties = new Properties();
        }


        /**
         * @return true if this container currently has no properties
         */
        public boolean isEmpty() {
            return this.heldProperties.isEmpty();
        }

        /**
         * @param key
         * @return true if a property with the given key exists in this container
         * @throws IllegalArgumentException if the given key is null
         */
        public boolean containsKey(String key) {
            validateKey(key);

            return this.heldProperties.containsKey(key);
        }

        /**
         * @param key
         * @return the current value of the property with the given key, or null if no property exists with that key
         * @throws IllegalArgumentException if the given key is null
         */
        public String getProperty(String key) {
            validateKey(key);

            return this.heldProperties.getProperty(key);
        }


        /**
         * Associates the given value with the given key
         *
         * @param key
         * @param value
         * @throws IllegalArgumentException if the given key is null
         * @throws IllegalArgumentException if the given value is null
         * @throws DuplicateKeyException if a property with the given key already exists
         */
        public void setProperty(String key, String value) {
        setProperty(null, key, value);
        }

        /**
         * Associates the given value with the given key
         *
         * @param source
         * @param key
         * @param value
         * @throws IllegalArgumentException if the given key is null
         * @throws IllegalArgumentException if the given value is null
         * @throws DuplicateKeyException if a property with the given key already exists
         */
        public void setProperty(PropertySource source, String key, String value) {
            validateKey(key);
            validateValue(value);

            if (containsKey(key)) {
                if (source != null && source instanceof FilePropertySource && ((FilePropertySource)source).isAllowOverrides()) {
                    LOG.info("Duplicate Key: Override is enabled [key=" + key + ", new value=" + value + ", old value=" + this.heldProperties.getProperty(key) + "]");
                } else {
                    throw new DuplicateKeyException("duplicate key '" + key + "'");
                }
            }
            this.heldProperties.setProperty(key, value);
        }

        /**
         * Removes the property with the given key from this container
         *
         * @param key
         * @throws IllegalArgumentException if the given key is null
         */
        public void clearProperty(String key) {
            validateKey(key);

            this.heldProperties.remove(key);
        }


        /**
         * Copies all name,value pairs from the given PropertySource instance into this container.
         *
         * @param source
         * @throws IllegalStateException if the source is invalid (improperly initialized)
         * @throws DuplicateKeyException the first time a given property has the same key as an existing property
         * @throws PropertiesException if unable to load properties from the given source
         */
        public void loadProperties(PropertySource source) {
            if (source == null) {
                throw new IllegalArgumentException("invalid (null) source");
            }

            Properties newProperties = source.loadProperties();

            for (Iterator i = newProperties.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                setProperty(source, key, newProperties.getProperty(key));
            }
        }

        /**
         * Removes all properties from this container.
         */
        public void clearProperties() {
            this.heldProperties.clear();
        }


        /**
         * @return iterator over the keys of all properties in this container
         */
        public Iterator getKeys() {
            return this.heldProperties.keySet().iterator();
        }


        /**
         * @param key
         * @throws IllegalArgumentException if the given key is null
         */
        private void validateKey(String key) {
            if (key == null) {
                throw new IllegalArgumentException("invalid (null) key");
            }
        }

        /**
         * @param value
         * @throws IllegalArgumentException if the given value is null
         */
        private void validateValue(String value) {
            if (value == null) {
                throw new IllegalArgumentException("invalid (null) value");
            }
        }


        public Properties getHeldProperties() {
            return heldProperties;
        }


        public void setHeldProperties(Properties heldProperties) {
            this.heldProperties = heldProperties;
        }
    }

    /**
     * This class is used to obtain properties from a properites file.
     *
     *
     */
    static class FilePropertySource implements PropertySource {
        private static Log log = LogFactory.getLog(FilePropertySource.class);


        private String fileName;
        private boolean allowOverrides;

        /**
         * Set source fileName.
         *
         * @param fileName
         */
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        /**
         * @return source fileName
         */
        public String getFileName() {
            return this.fileName;
        }

        public boolean isAllowOverrides() {
            return this.allowOverrides;
        }

        public void setAllowOverrides(boolean allowOverrides) {
            this.allowOverrides = allowOverrides;
        }

        /**
         * Attempts to load properties from a properties file which has the current fileName and is located on the classpath.
         *
         * @see ConfigurationServiceImpl.PropertySource#loadProperties()
         * @throws IllegalStateException if the fileName is null or empty
         */
        public Properties loadProperties() {
            if (StringUtils.isBlank(getFileName())) {
                throw new IllegalStateException("invalid (blank) fileName");
            }

            Properties properties = new Properties();

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL url = loader.getResource(getFileName());
            if (url == null) {
                throw new PropertiesException("unable to locate properties file '" + getFileName() + "'");
            }

            InputStream in = null;

            try {
                in = url.openStream();
                properties.load(in);
            }
            catch (IOException e) {
                throw new PropertiesException("error loading from properties file '" + getFileName() + "'", e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        log.error("caught exception closing InputStream: " + e);
                    }

                }
            }

            return properties;
        }

    }
}
