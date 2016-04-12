/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.uif.UifConstants;

/**
 * Validation pattern for matching floating point numbers, optionally matching negative numbers
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FloatingPointPatternConstraint extends ConfigurationBasedRegexPatternConstraint {

    protected boolean allowNegative;

    /**
     * @see ValidCharactersPatternConstraint#getRegexString()
     */
    @Override
    protected String getRegexString() {
        StringBuffer regex = new StringBuffer();

        if (isAllowNegative()) {
            regex.append("-?");
        }
        regex.append(super.getRegexString());

        return regex.toString();
    }

    /**
     * @return the allowNegative
     */
    public boolean isAllowNegative() {
        return this.allowNegative;
    }

    /**
     * @param allowNegative the allowNegative to set
     */
    public void setAllowNegative(boolean allowNegative) {
        this.allowNegative = allowNegative;
    }

    /**
     * This overridden method ...
     * 
     * @see ValidDataPatternConstraint#getValidationMessageParams()
     */
    @Override
    public List<String> getValidationMessageParams() {
        if (validationMessageParams == null) {
            validationMessageParams = new ArrayList<String>();
            ConfigurationService configService = KRADServiceLocator.getKualiConfigurationService();
            if (allowNegative) {
                validationMessageParams.add(configService
                        .getPropertyValueAsString(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX
                                + "positiveOrNegative"));
            } else {
                validationMessageParams.add(configService
                        .getPropertyValueAsString(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "positive"));
            }
        }
        return validationMessageParams;
    }
}
