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
package org.kuali.kfs.coa.businessobject.inquiry;

import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.kns.inquiry.KualiInquirableImpl;
import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.kns.web.ui.Row;
import org.kuali.kfs.kns.web.ui.Section;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.ArrayList;
import java.util.List;

public class OrganizationReversionInquirable extends KualiInquirableImpl {
    private OrganizationReversionService organizationReversionService;

    /**
     * Overridden to take out details with inactive categories
     *
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.krad.bo.BusinessObject)
     * <p>
     * KRAD Conversion: Inquirable performs conditional display/hiding of the fields/sections on the inquiry
     * But all field/section definitions are in data dictionary for bo Organization.
     */
    @Override
    public List<Section> getSections(BusinessObject bo) {
        List<Section> sections = super.getSections(bo);
        if (organizationReversionService == null) {
            organizationReversionService = SpringContext.getBean(OrganizationReversionService.class);
        }
        for (Section section : sections) {
            for (Row row : section.getRows()) {
                List<Field> updatedFields = new ArrayList<Field>();
                for (Field field : row.getFields()) {
                    if (shouldIncludeField(field)) {
                        updatedFields.add(field);
                    }
                }
                row.setFields(updatedFields);
            }
        }
        return sections;
    }

    /**
     * Determines if the given field should be included in the updated row, once we take out inactive categories
     *
     * @param field the field to check
     * @return true if the field should be included (ie, it doesn't describe an organization reversion with an inactive category); false otherwise
     * <p>
     * KRAD Conversion: Determines if fields should be included in the section.
     * But all field/section definitions are in data dictionary.
     */
    protected boolean shouldIncludeField(Field field) {
        boolean includeField = true;
        if (field.getContainerRows() != null) {
            for (Row containerRow : field.getContainerRows()) {
                for (Field containedField : containerRow.getFields()) {
                    if (containedField.getPropertyName().matches("organizationReversionDetail\\[\\d+\\]\\.organizationReversionCategoryCode")) {
                        final String categoryValue = containedField.getPropertyValue();
                        includeField = organizationReversionService.isCategoryActive(categoryValue);
                    }
                }
            }
        }
        return includeField;
    }
}
