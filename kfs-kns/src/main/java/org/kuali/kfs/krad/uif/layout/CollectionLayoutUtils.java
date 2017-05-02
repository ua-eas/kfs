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
package org.kuali.kfs.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.UifPropertyPaths;
import org.kuali.kfs.krad.uif.component.DataBinding;
import org.kuali.kfs.krad.uif.container.CollectionGroup;
import org.kuali.kfs.krad.uif.control.Control;
import org.kuali.kfs.krad.uif.control.ValueConfiguredControl;
import org.kuali.kfs.krad.uif.field.Field;
import org.kuali.kfs.krad.uif.field.InputField;
import org.kuali.kfs.krad.util.KRADUtils;

/**
 * Utilities for collection layout managers
 */
public class CollectionLayoutUtils {

    public static void prepareSelectFieldForLine(Field selectField, CollectionGroup collectionGroup, String lineBindingPath,
                                                 Object line) {
        // if select property name set use as property name for select field
        String selectPropertyName = collectionGroup.getSelectPropertyName();
        if (StringUtils.isNotBlank(selectPropertyName)) {
            // if select property contains form prefix, will bind to form and not each line
            if (selectPropertyName.startsWith(UifConstants.NO_BIND_ADJUST_PREFIX)) {
                selectPropertyName = StringUtils.removeStart(selectPropertyName, UifConstants.NO_BIND_ADJUST_PREFIX);
                ((DataBinding) selectField).getBindingInfo().setBindingName(selectPropertyName);
                ((DataBinding) selectField).getBindingInfo().setBindToForm(true);

                setControlValueToLineIdentifier(selectField, line);
            } else {
                ((DataBinding) selectField).getBindingInfo().setBindingName(selectPropertyName);
                ((DataBinding) selectField).getBindingInfo().setBindByNamePrefix(lineBindingPath);
            }
        } else {
            // select property name not given, use UifFormBase#selectedCollectionLines
            String collectionLineKey = KRADUtils.translateToMapSafeKey(
                collectionGroup.getBindingInfo().getBindingPath());
            String selectBindingPath = UifPropertyPaths.SELECTED_COLLECTION_LINES + "['" + collectionLineKey + "']";

            ((DataBinding) selectField).getBindingInfo().setBindingName(selectBindingPath);
            ((DataBinding) selectField).getBindingInfo().setBindToForm(true);

            setControlValueToLineIdentifier(selectField, line);
        }
    }

    protected static void setControlValueToLineIdentifier(Field selectField, Object line) {
        if (selectField instanceof InputField) {
            Control selectControl = ((InputField) selectField).getControl();

            selectControl.addStyleClass("kr-select-line");

            if ((selectControl != null) && (selectControl instanceof ValueConfiguredControl)) {
                String lineIdentifier =
                    KRADServiceLocatorWeb.getDataObjectMetaDataService().getDataObjectIdentifierString(line);
                ((ValueConfiguredControl) selectControl).setValue(lineIdentifier);
            }
        }
    }
}
