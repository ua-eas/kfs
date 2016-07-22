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
 * TODO delyea don't forget to fill this in.
 * 
 * 
 */
public class FixedPointPatternConstraint extends ValidDataPatternConstraint {

    protected boolean allowNegative;
    protected int precision;
    protected int scale;

    /**
     * Overriding retrieval of
     * 
     * @see ValidCharactersPatternConstraint#getRegexString()
     */
    @Override
    protected String getRegexString() {
        StringBuilder regex = new StringBuilder();

        if (isAllowNegative()) {
            regex.append("-?");
        }
        // final patter will be: -?([0-9]{0,p-s}\.[0-9]{1,s}|[0-9]{1,p-s}) where p = precision, s=scale
        regex.append("(");
        regex.append("[0-9]{0," + (getPrecision() - getScale()) + "}");
        regex.append("\\.");
        regex.append("[0-9]{1," + getScale() + "}");
        regex.append("|[0-9]{1," + (getPrecision() - getScale()) + "}");
        regex.append(")");
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
     * @return the precision
     */
    public int getPrecision() {
        return this.precision;
    }

    /**
     * @param precision the precision to set
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /**
     * @return the scale
     */
    public int getScale() {
        return this.scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

    /**
     * This overridden method ...
     * 
     * @see ValidDataPatternConstraint#getValidationMessageParams()
     */
    @Override
    public List<String> getValidationMessageParams() {
        if(validationMessageParams == null){
            validationMessageParams = new ArrayList<String>();
            ConfigurationService configService = KRADServiceLocator.getKualiConfigurationService();
            if (allowNegative) {
                validationMessageParams.add(configService.getPropertyValueAsString(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX
                        + "positiveOrNegative"));
            } else {
                validationMessageParams.add(configService.getPropertyValueAsString(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX
                        + "positive"));
            }
    
            validationMessageParams.add(Integer.toString(precision));
            validationMessageParams.add(Integer.toString(scale));
        }
        return validationMessageParams;
    }

}
