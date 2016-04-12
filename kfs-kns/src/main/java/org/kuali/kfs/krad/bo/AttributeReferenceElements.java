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
package org.kuali.kfs.krad.bo;


import org.kuali.kfs.krad.bo.PersistableBusinessObjectExtensionBase;

/**
 * Attribute Reference Dummy Business Object
 */
public class AttributeReferenceElements extends PersistableBusinessObjectExtensionBase {
    private static final long serialVersionUID = -7646503995716194181L;
    private String infoTextArea;
    private String extendedTextArea;

    /**
     * 
     * Constructs a AttributeReferenceDummy.java.
     * 
     */
    public AttributeReferenceElements() {
    }

    /**
     * Gets the infoTextArea attribute.
     * 
     * @return Returns the infoTextArea.
     */
    public String getInfoTextArea() {
        return infoTextArea;
    }

    /**
     * Sets the infoTextArea attribute value.
     * 
     * @param infoTextArea The infoTextArea to set.
     */
    public void setInfoTextArea(String infoTextArea) {
        this.infoTextArea = infoTextArea;
    }

    /**
     * @return the extendedTextArea
     */
    public final String getExtendedTextArea() {
        return this.extendedTextArea;
    }

    /**
     * @param extendedTextArea the extendedTextArea to set
     */
    public final void setExtendedTextArea(String extendedTextArea) {
        this.extendedTextArea = extendedTextArea;
    }
}
