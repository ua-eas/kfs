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
package org.kuali.kfs.kns.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.inquiry.InquiryAuthorizer;
import org.kuali.kfs.kns.inquiry.InquiryPresentationController;
import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinition;
import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.kfs.krad.inquiry.Inquirable;

import java.util.ArrayList;
import java.util.List;

/**
 * The inquiry element is used to specify the fields that will be displayed on the
 * inquiry screen for this business object and the order in which they will appear.
 * <p>
 * JSTL: The inquiry element is a Map which is accessed using
 * a key of "inquiry".  This map contains the following keys:
 * title (String)
 * inquiryFields (Map)
 */
@Deprecated
public class InquiryDefinition extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = -2506403061297774668L;

    protected String title;
    protected List<InquirySectionDefinition> inquirySections = new ArrayList<InquirySectionDefinition>();
    protected Class<? extends Inquirable> inquirableClass;
    protected Class<? extends InquiryPresentationController> presentationControllerClass;
    protected Class<? extends InquiryAuthorizer> authorizerClass;

    protected boolean translateCodes = true;

    public InquiryDefinition() {
    }


    public String getTitle() {
        return title;
    }

    /**
     * The title element is used specify the title that will appear in the header
     * of an Inquiry or Lookup screen.
     *
     * @throws IllegalArgumentException if the given title is blank
     */
    public void setTitle(String title) {
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("invalid (blank) title");
        }

        this.title = title;
    }

    /**
     * @return Collection of all inquiryField FieldDefinitions associated with this InquiryDefinition, in the order in which they
     * were added
     */
    public List<InquirySectionDefinition> getInquirySections() {
        return inquirySections;
    }

    /**
     * Returns the FieldDefinition associated with the field attribute name
     *
     * @param fieldName
     * @return
     */
    public FieldDefinition getFieldDefinition(String fieldName) {
        for (InquirySectionDefinition section : inquirySections) {
            for (FieldDefinition field : section.getInquiryFields()) {
                if (field.getAttributeName().equals(fieldName)) {
                    return field;
                }
            }
        }

        return null;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     *
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        for (InquirySectionDefinition inquirySection : inquirySections) {
            inquirySection.completeValidation(rootBusinessObjectClass, null);
        }
    }

    public InquirySectionDefinition getInquirySection(String sectionTitle) {
        for (InquirySectionDefinition inquirySection : inquirySections) {
            if (inquirySection.getTitle().equals(sectionTitle)) {
                return inquirySection;
            }
        }
        return null;
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "InquiryDefinition '" + getTitle() + "'";
    }


    public Class<? extends Inquirable> getInquirableClass() {
        return inquirableClass;
    }

    /**
     * inquirableClass is required if a custom inquirable is required which will show
     * additional data other than the business object attributes.
     * <p>
     * Example from Org.xml:
     * <inquirableClass>org.kuali.module.chart.maintenance.OrgInquirable</inquirableClass>
     * The custom inquirable is required in this case because the organization hierarchy
     * is shown on the inquiry screen.
     */
    public void setInquirableClass(Class<? extends Inquirable> inquirableClass) {
        this.inquirableClass = inquirableClass;
    }

    /**
     * inquirySections allows inquiry to be presented in sections.
     * Each section can have a different format.
     */
    public void setInquirySections(List<InquirySectionDefinition> inquirySections) {
        this.inquirySections = inquirySections;
    }


    public Class<? extends InquiryPresentationController> getPresentationControllerClass() {
        return this.presentationControllerClass;
    }


    public void setPresentationControllerClass(
        Class<? extends InquiryPresentationController> presentationControllerClass) {
        this.presentationControllerClass = presentationControllerClass;
    }


    public Class<? extends InquiryAuthorizer> getAuthorizerClass() {
        return this.authorizerClass;
    }


    public void setAuthorizerClass(
        Class<? extends InquiryAuthorizer> authorizerClass) {
        this.authorizerClass = authorizerClass;
    }


    public boolean isTranslateCodes() {
        return this.translateCodes;
    }


    public void setTranslateCodes(boolean translateCodes) {
        this.translateCodes = translateCodes;
    }

}
