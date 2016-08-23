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
package org.kuali.kfs.kns.web.struts.action;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.util.PropertyMessageResources;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in.
 */
public class KualiPropertyMessageResources extends PropertyMessageResources {
    private static final long serialVersionUID = -7712311580595112293L;
    private HashMap kualiMessages;

    public KualiPropertyMessageResources(MessageResourcesFactory factory, String config) {
        super(factory, config);
    }

    public KualiPropertyMessageResources(MessageResourcesFactory factory, String config, boolean returnNull) {
        super(factory, config, returnNull);
    }

    protected void loadLocale(String localeKey) {
        String initialConfig = config;
        String[] propertyFiles = config.split(",");
        for (String propertyFile : propertyFiles) {
            config = propertyFile;
            locales.remove(localeKey);
            super.loadLocale(localeKey);
        }
        config = initialConfig;
    }

    public Map getKualiProperties(String localeKey) {
        if (this.kualiMessages != null && !this.kualiMessages.isEmpty()) {
            return this.kualiMessages;
        }
        localeKey = (localeKey == null) ? "" : localeKey;
        String localePrefix = localeKey + ".";

        this.loadLocale((localeKey == null) ? "" : localeKey);
        this.kualiMessages = new HashMap(this.messages.size());
        Set<String> keys = this.messages.keySet();
        for (String key : keys) {
            this.kualiMessages.put(StringUtils.substringAfter(key, localePrefix), this.messages.get(key));
        }
        return this.kualiMessages;
    }

}
