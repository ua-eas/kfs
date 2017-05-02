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
package org.kuali.kfs.kns.lookup;

import org.displaytag.export.ExcelView;
import org.displaytag.model.TableModel;
import org.kuali.kfs.krad.util.KRADConstants;

import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.io.Writer;

/**
 * This class allows for plugging in custom XML export into the Display Tag library.
 */
public class CustomExcelExportView extends ExcelView {

    private ExportViewHelper helper;

    @Override
    public void setParameters(TableModel tableModel, boolean exportFullList, boolean includeHeader, boolean decorateValues) {
        this.helper = new ExportViewHelper(tableModel);
        super.setParameters(tableModel, exportFullList, includeHeader, decorateValues);
    }

    @Override
    public void doExport(Writer writer) throws IOException, JspException {
        if (!helper.attemptCustomExport(writer, KRADConstants.EXCEL_FORMAT)) {
            super.doExport(writer);
        }
    }

}
