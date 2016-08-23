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
package org.kuali.kfs.krad.datadictionary.mask;

import java.io.Serializable;

/**
    The displayMask element specifies the type of masking to
    be used to hide the value from un-authorized users.
    There are three types of masking.
 */
public class Mask implements Serializable {
    private static final long serialVersionUID = 4035984416568235531L;

	protected MaskFormatter maskFormatter;
    protected Class<? extends MaskFormatter> maskFormatterClass;

    /**
     * Masks a data value with the configured maskFormatter;
     * @param value of the object
     * @return string value of the masked object
     */
    public String maskValue(Object value) {
        if (maskFormatter == null) {
            if (maskFormatterClass != null) {
                try {
                    maskFormatter = maskFormatterClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Unable to create instance of mask formatter class: " + maskFormatterClass.getName());
                }
            }
            else {
                throw new RuntimeException("Mask formatter not set for secure field.");
            }
        }

        return maskFormatter.maskValue(value);
    }

    /**
     * Gets the maskFormatter attribute.
     *
     * @return Returns the maskFormatter.
     */
    public MaskFormatter getMaskFormatter() {
        return maskFormatter;
    }

    /**
     *
     * @param maskFormatter instance to be used for masking field values.
     */
    public void setMaskFormatter(MaskFormatter maskFormatter) {
        this.maskFormatter = maskFormatter;
    }

    /**
     * Gets the maskFormatterClass attribute.
     *
     * @return Returns the maskFormatterClass.
     */
    public Class<? extends MaskFormatter> getMaskFormatterClass() {
        return maskFormatterClass;
    }

    /**
     * @param maskFormatterClass element is used when a custom masking
     * algorithm is desired.  This element specifies the name of a
     * class that will perform the masking for unauthorized users.
     */
    public void setMaskFormatterClass(Class<? extends MaskFormatter> maskFormatterClass) {
        this.maskFormatterClass = maskFormatterClass;
    }

}
