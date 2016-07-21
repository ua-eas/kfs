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
package org.kuali.kfs.krad.datadictionary;

import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.mask.MaskFormatter;

/**
 * Defines a set of restrictions that are possible on an attribute
 * 
 * 
 */
public class AttributeSecurity extends DataDictionaryDefinitionBase {
	private static final long serialVersionUID = -7923499408946975318L;
	
	private boolean readOnly = false;
	private boolean hide = false;
	private boolean mask = false;
	private boolean partialMask = false;

	private MaskFormatter partialMaskFormatter;
	private MaskFormatter maskFormatter;

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return this.readOnly;
	}

	/**
	 * @param readOnly
	 *            the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @return the hide
	 */
	public boolean isHide() {
		return this.hide;
	}

	/**
	 * @param hide
	 *            the hide to set
	 */
	public void setHide(boolean hide) {
		this.hide = hide;
	}

	/**
	 * @return the mask
	 */
	public boolean isMask() {
		return this.mask;
	}

	/**
	 * @param mask
	 *            the mask to set
	 */
	public void setMask(boolean mask) {
		this.mask = mask;
	}

	/**
	 * @return the partialMask
	 */
	public boolean isPartialMask() {
		return this.partialMask;
	}

	/**
	 * @param partialMask
	 *            the partialMask to set
	 */
	public void setPartialMask(boolean partialMask) {
		this.partialMask = partialMask;
	}

	/**
	 * @return the maskFormatter
	 */
	public MaskFormatter getMaskFormatter() {
		return this.maskFormatter;
	}

	/**
	 * @param maskFormatter
	 *            the maskFormatter to set
	 */
	public void setMaskFormatter(MaskFormatter maskFormatter) {
		this.maskFormatter = maskFormatter;
	}

	/**
	 * @return the partialMaskFormatter
	 */
	public MaskFormatter getPartialMaskFormatter() {
		return this.partialMaskFormatter;
	}

	/**
	 * @param partialMaskFormatter
	 *            the partialMaskFormatter to set
	 */
	public void setPartialMaskFormatter(MaskFormatter partialMaskFormatter) {
		this.partialMaskFormatter = partialMaskFormatter;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see DataDictionaryDefinition#completeValidation(java.lang.Class,
	 *      java.lang.Class)
	 */
	public void completeValidation(Class rootBusinessObjectClass,
			Class otherBusinessObjectClass) {

		if (mask && maskFormatter == null) {
			throw new AttributeValidationException("MaskFormatter is required");
		}
		if (partialMask && partialMaskFormatter == null) {
			throw new AttributeValidationException(
					"PartialMaskFormatter is required");
		}
	}

	/**
	 * Returns whether any of the restrictions defined in this class are true.
	 */
	public boolean hasAnyRestriction() {
		return readOnly || mask || partialMask || hide;
	}
	
	
	/**
	 * Returns whether any of the restrictions defined in this class indicate that the attribute value potentially needs
	 * to be not shown to the user (i.e. masked, partial mask, hide).  Note that readonly does not fall in this category.
	 * 
	 * @return
	 */
	public boolean hasRestrictionThatRemovesValueFromUI() {
		return mask || partialMask || hide;	
	}
}
