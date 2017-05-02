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
package org.kuali.kfs.sys.document.service;

import org.kuali.kfs.sys.document.web.AccountingLineTableRow;

import java.util.List;

/**
 * A contract of services that wish to transform accounting line renderable elements after their tablification
 */
public interface AccountingLineTableTransformation {
    /**
     * Performs transformation to the tablified rows for an accounting line
     *
     * @param rows the tablified rows that represent a renderable accounting line
     */
    public abstract void transformRows(List<AccountingLineTableRow> rows);
}
