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
package org.kuali.kfs.kns.lookup;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.util.WriterOutputStream;
import org.displaytag.model.Row;
import org.displaytag.model.TableModel;
import org.kuali.kfs.kns.util.KNSGlobalVariables;
import org.kuali.kfs.kns.web.struts.form.KualiForm;
import org.kuali.kfs.kns.web.struts.form.LookupForm;
import org.kuali.kfs.kns.web.ui.ResultRow;
import org.kuali.kfs.krad.bo.Exporter;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.exception.ExportNotSupportedException;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.bo.BusinessObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper class to be used with the custom ExportView implementations for
 * Display Tag.  Most of the logic for interfacing with the KNS export
 * system is encapsulated in this helper class so it can be shared between
 * the various Display Tag export implementations.
 */
public class ExportViewHelper {

    private BusinessObjectEntry businessObjectEntry;
    private List<BusinessObject> businessObjects;

    public ExportViewHelper(TableModel tableModel) {
        this.businessObjectEntry = loadBusinessObjectEntry();
        this.businessObjects = loadBusinessObjects(tableModel);
    }

    protected BusinessObjectEntry loadBusinessObjectEntry() {
        KualiForm kualiForm = KNSGlobalVariables.getKualiForm();
        if (kualiForm instanceof LookupForm) {
            LookupForm lookupForm = (LookupForm) kualiForm;
            if (!StringUtils.isBlank(lookupForm.getBusinessObjectClassName())) {
                return KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(lookupForm.getBusinessObjectClassName());
            }
        }
        return null;
    }

    protected List<BusinessObject> loadBusinessObjects(TableModel tableModel) {
        List<BusinessObject> businessObjects = new ArrayList<BusinessObject>();
        List<Row> rowList = tableModel.getRowListFull();
        for (Row row : rowList) {
            ResultRow resultRow = (ResultRow) row.getObject();
            if (resultRow.getBusinessObject() != null) {
                businessObjects.add(resultRow.getBusinessObject());
            }
        }
        return businessObjects;
    }

    public BusinessObjectEntry getBusinessObjectEntry() {
        return businessObjectEntry;
    }

    public List<BusinessObject> getBusinessObjects() {
        return businessObjects;
    }

    public boolean attemptCustomExport(OutputStream outputStream, String exportFormat) throws IOException {
        if (getBusinessObjectEntry() != null && getBusinessObjectEntry().getExporterClass() != null) {
            final Exporter exporter;
            try {
                exporter = getBusinessObjectEntry().getExporterClass().newInstance();
            } catch (Exception e) {
                throw new ExportNotSupportedException("Failed to load export class: " + businessObjectEntry.getExporterClass(), e);
            }
            List<String> supportedFormats = exporter.getSupportedFormats(businessObjectEntry.getBusinessObjectClass());
            if (supportedFormats.contains(exportFormat)) {
                exporter.export(businessObjectEntry.getBusinessObjectClass(), getBusinessObjects(), exportFormat, outputStream);
                return true;
            }
        }
        return false;
    }

    public boolean attemptCustomExport(Writer writer, String exportFormat) throws IOException {
        return attemptCustomExport(new WriterOutputStream(writer), exportFormat);
    }

}
