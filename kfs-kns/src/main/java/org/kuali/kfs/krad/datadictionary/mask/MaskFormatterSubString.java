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
package org.kuali.kfs.krad.datadictionary.mask;

import org.apache.commons.lang.StringUtils;

/**
 * The maskTo element is to used hide the beginning part of the value for
 * unauthorized users. The number of leading characters to hide and the
 * replacement character can be specified.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaskFormatterSubString implements MaskFormatter {
    private static final long serialVersionUID = -876112522775686636L;
    
    protected String maskCharacter = "*";
    protected int maskLength;

    public String maskValue(Object value) {
        if (value == null) {
            return null;
        }

        // TODO: MOVE TO UNIT TEST: move this validation into the unit tests
        if (maskCharacter == null) {
            throw new RuntimeException("Mask character not specified. Check DD maskTo attribute.");
        }

        String strValue = value.toString();
        if (strValue.length() < maskLength) {
        	return StringUtils.repeat(maskCharacter, maskLength);
        }
        if(maskLength >0){
        	return StringUtils.repeat(maskCharacter, maskLength) + strValue.substring(maskLength);
        }else{
        	return strValue;
        }
    }

    /**
     * Gets the maskCharacter attribute.
     * 
     * @return Returns the maskCharacter.
     */
    public String getMaskCharacter() {
        return maskCharacter;
    }

    /**
     * Specify the character with which to mask the original value.
     *
     * @param maskCharacter for masking values
     */
    public void setMaskCharacter(String maskCharacter) {
        this.maskCharacter = maskCharacter;
    }

    /**
     * Gets the maskLength attribute.
     * 
     * @return Returns the maskLength.
     */
    public int getMaskLength() {
        return maskLength;
    }

    /**
     * Set the number of characters to mask at the beginning of the string.
     * 
     * @param maskLength The maskLength to set.
     */
    public void setMaskLength(int maskLength) {
        this.maskLength = maskLength;
    }


}
