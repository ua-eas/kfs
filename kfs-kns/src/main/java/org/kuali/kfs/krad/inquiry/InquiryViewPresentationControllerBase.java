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
package org.kuali.kfs.krad.inquiry;

import org.kuali.kfs.krad.bo.Exporter;
import org.kuali.kfs.krad.datadictionary.DataObjectEntry;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.view.InquiryView;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.view.ViewPresentationController;
import org.kuali.kfs.krad.uif.view.ViewPresentationControllerBase;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.web.form.UifFormBase;

import java.util.Set;

/**
 * Implementation of {@link ViewPresentationController} for
 * {@link InquiryView} instances
 * <p>
 * <p>
 * Adds flag for export of inquiry record
 * </p>
 */
public class InquiryViewPresentationControllerBase extends ViewPresentationControllerBase {
    private static final long serialVersionUID = 7504225899471226403L;

    /**
     * @see ViewPresentationController#getActionFlags(View,
     * UifFormBase)
     */
    @Override
    public Set<String> getActionFlags(View view, UifFormBase model) {
        Set<String> actionFlags = super.getActionFlags(view, model);

        if (isExportSupported((InquiryView) model.getView())) {
            actionFlags.add(KRADConstants.KUALI_ACTION_CAN_EXPORT);
        }

        return actionFlags;
    }

    /**
     * Examines the data objects data dictionary entry to determine if it supports XML export or not
     *
     * @return boolean true if it supports export, false if not
     */
    protected boolean isExportSupported(InquiryView view) {
        DataObjectEntry dataObjectEntry =
            KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDataObjectEntry(
                view.getDataObjectClassName().getName());

        Class<? extends Exporter> exporterClass = dataObjectEntry.getExporterClass();
        if (exporterClass != null) {
            try {
                Exporter exporter = exporterClass.newInstance();
                if (exporter.getSupportedFormats(dataObjectEntry.getDataObjectClass()).contains(
                    KRADConstants.XML_FORMAT)) {
                    return true;
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to locate or create exporter class: " + exporterClass);
            }
        }

        return false;
    }
}
