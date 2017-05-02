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
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.uif.UifConstants;

/**
 *
 *
 */
public class ConfigurationBasedRegexPatternConstraint extends ValidDataPatternConstraint {
    /**
     * the key used to identify the validation pattern
     */
    protected String patternTypeKey;

    /**
     * the key used to identify the validation pattern
     *
     * @return the patternTypeKey
     */
    public String getPatternTypeKey() {
        return this.patternTypeKey;
    }

    /**
     * the key used to identify the validation pattern
     *
     * @param patternTypeKey the patternTypeKey to set
     */
    public void setPatternTypeKey(String patternTypeKey) {
        this.patternTypeKey = patternTypeKey;
    }

    /**
     * This overridden method ...
     *
     * @see BaseConstraint#getLabelKey()
     */
    @Override
    public String getLabelKey() {
        if (StringUtils.isNotEmpty(labelKey)) {
            return labelKey;
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX).append(getPatternTypeKey());
            return buf.toString();
        }
    }

    /**
     * This method implementation uses the key returned by {@link #getPatternTypePropertyString()} to fetch the
     * validationPattern's regex string from the ConfigurationService which should not include the start(^) and end($) symbols
     */
    protected String getRegexString() {
//        return (String) KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString("validationPatternRegex." + getPatternTypeName());
        return (String) KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(getPatternTypeKey());
    }

}
