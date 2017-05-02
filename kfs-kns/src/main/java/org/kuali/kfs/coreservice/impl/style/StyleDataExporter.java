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
package org.kuali.kfs.coreservice.impl.style;

import org.kuali.kfs.krad.bo.Exporter;
import org.kuali.kfs.krad.exception.ExportNotSupportedException;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.impex.ExportDataSet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the {@link Exporter} class which facilitates exporting
 * of {@link StyleBo} data from the GUI.
 *
 * @see ExportDataSet
 * @see StyleBo
 */
public class StyleDataExporter implements Exporter {

    private List<String> supportedFormats = new ArrayList<String>();

    public StyleDataExporter() {
        supportedFormats.add(KRADConstants.XML_FORMAT);
    }

    @Override
    public void export(Class<?> dataObjectClass,
                       List<? extends Object> dataObjects, String exportFormat,
                       OutputStream outputStream) throws IOException {
        if (!KRADConstants.XML_FORMAT.equals(exportFormat)) {
            throw new ExportNotSupportedException("The given export format of "
                + exportFormat
                + " is not supported by the KEW XML Exporter!");
        }
        ExportDataSet dataSet = buildExportDataSet(dataObjectClass, dataObjects);
        outputStream.write(CoreApiServiceLocator.getXmlExporterService()
            .export(dataSet));
        outputStream.flush();
    }

    @Override
    public List<String> getSupportedFormats(Class<?> dataObjectClass) {
        return supportedFormats;
    }

    /**
     * Builds the ExportDataSet based on the BusinessObjects passed in.
     */
    protected ExportDataSet buildExportDataSet(Class<?> dataObjectClass,
                                               List<? extends Object> dataObjects) {
        StyleExportDataSet dataSet = new StyleExportDataSet();
        for (Object dataObject : dataObjects) {
            if (dataObjectClass.equals(StyleBo.class)) {
                dataSet.getStyles().add((StyleBo) dataObject);
            }
        }

        return dataSet.createExportDataSet();
    }

}
