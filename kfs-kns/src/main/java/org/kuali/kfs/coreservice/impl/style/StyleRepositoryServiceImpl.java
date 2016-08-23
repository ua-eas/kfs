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
package org.kuali.kfs.coreservice.impl.style;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coreservice.api.style.Style;
import org.kuali.kfs.coreservice.api.style.StyleRepositoryService;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.util.RiceUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;


/**
 * Implements generic StyleService via existing EDL style table
 */
public class StyleRepositoryServiceImpl implements StyleRepositoryService {
    private static final Logger LOG = Logger.getLogger(StyleRepositoryServiceImpl.class);

    private static final String STYLE_CONFIG_PREFIX = "edl.style";

    private StyleXmlParser styleXmlParser;
    private StyleDao styleDao;

    public void setStyleDao(StyleDao styleDao) {
        this.styleDao = styleDao;
    }

    public void setStyleXmlParser(StyleXmlParser styleXmlParser) {
        this.styleXmlParser = styleXmlParser;
    }

    /**
     * Loads the named style from the database, or (if configured) imports it from a file
     * specified via a configuration parameter with a name of the format edl.style.&lt;styleName&gt;
     * {@inheritDoc}
     */
    @Override
    public Style getStyle(String styleName) {
        if (StringUtils.isBlank(styleName)) {
            throw new RiceIllegalArgumentException("styleName was null or blank");
        }

        // try to fetch the style from the database
        StyleBo style = styleDao.getStyle(styleName);
        // if it's null, look for a config param specifiying a file to load
        if (style == null) {
            String propertyName = STYLE_CONFIG_PREFIX + "." + styleName;
            String location = ConfigContext.getCurrentContextConfig().getProperty(propertyName);
            if (location != null) {

                final InputStream xml;

                try {
                    xml = RiceUtilities.getResourceAsStream(location);
                } catch (MalformedURLException e) {
                    throw new RiceRuntimeException(getUnableToLoadMessage(propertyName, location), e);
                } catch (IOException e) {
                    throw new RiceRuntimeException(getUnableToLoadMessage(propertyName, location), e);
                }

                if (xml == null) {
                    throw new RiceRuntimeException(getUnableToLoadMessage(propertyName, location) + ", no such file");
                }

                LOG.info("Automatically loading style '" + styleName + "' from '" + location + "' as configured by " + propertyName);
                List<Style> styles = styleXmlParser.parseStyles(xml);
                for (Style autoLoadedStyle : styles) {
                    if (autoLoadedStyle.getName().equals(styleName)) {
                        return autoLoadedStyle;
                    }
                }
                throw new RiceRuntimeException("Failed to locate auto-loaded style '" + styleName + "' after successful parsing of file from '" + location + "' as configured by " + propertyName);
            }
        }
        return StyleBo.to(style);
    }

    private String getUnableToLoadMessage(String propertyName, String location) {
        return "unable to load resource at '" + location +
                "' specified by configuration parameter '" + propertyName + "'";
    }

    @Override
    public void saveStyle(Style data) {
        if (data == null) {
            throw new RiceIllegalArgumentException("The given style was null.");
        }
        StyleBo styleToUpdate = StyleBo.from(data);
        saveStyleBo(styleToUpdate);
    }

    protected void saveStyleBo(StyleBo styleBo) {
        StyleBo existingData = styleDao.getStyle(styleBo.getName());
        if (existingData != null) {
            existingData.setActive(false);
            styleDao.saveStyle(existingData);
        }
        styleDao.saveStyle(styleBo);
    }

    @Override
    public List<String> getAllStyleNames() {
        return styleDao.getAllStyleNames();
    }
}
