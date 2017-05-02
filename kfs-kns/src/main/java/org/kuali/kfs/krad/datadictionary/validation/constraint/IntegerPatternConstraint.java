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

import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Administrator don't forget to fill this in.
 */
public class IntegerPatternConstraint extends ValidDataPatternConstraint {
    protected boolean allowNegative;
    protected boolean onlyNegative;
    protected boolean omitZero;

    /**
     * @see ValidCharactersPatternConstraint#getRegexString()
     */
    @Override
    protected String getRegexString() {
        StringBuffer regex = new StringBuffer();

        if (isAllowNegative() && !onlyNegative) {
            regex.append("((-?");
        } else if (onlyNegative) {
            regex.append("((-");
        } else {
            regex.append("((");
        }
        if (omitZero) {
            regex.append("[1-9][0-9]*))");
        } else {
            regex.append("[1-9][0-9]*)|[0]*)");
        }

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

    public boolean isOnlyNegative() {
        return onlyNegative;
    }

    /**
     * When set to true, only allows negative numbers (and zero if allowZero is still true)
     *
     * @param onlyNegative
     */
    public void setOnlyNegative(boolean onlyNegative) {
        this.onlyNegative = onlyNegative;
    }

    public boolean isOmitZero() {
        return omitZero;
    }

    /**
     * When set to true, zero is not allowed in the set of allowed numbers.
     *
     * @param omitZero
     */
    public void setOmitZero(boolean omitZero) {
        this.omitZero = omitZero;
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
            if (allowNegative && !onlyNegative) {
                if (omitZero) {
                    validationMessageParams.add(configService
                        .getPropertyValueAsString(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX
                            + "positiveOrNegative"));
                } else {
                    validationMessageParams.add(configService
                        .getPropertyValueAsString(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX
                            + "positiveOrNegativeOrZero"));
                }
            } else if (onlyNegative) {
                if (omitZero) {
                    validationMessageParams.add(configService
                        .getPropertyValueAsString(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "negative"));
                } else {
                    validationMessageParams.add(configService
                        .getPropertyValueAsString(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "negativeOrZero"));
                }
            } else {
                if (omitZero) {
                    validationMessageParams.add(configService
                        .getPropertyValueAsString(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "positive"));
                } else {
                    validationMessageParams.add(configService
                        .getPropertyValueAsString(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "positiveOrZero"));
                }
            }
        }
        return validationMessageParams;
    }
}
