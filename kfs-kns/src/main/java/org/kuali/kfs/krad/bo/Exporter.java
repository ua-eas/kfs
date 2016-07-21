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

import org.kuali.kfs.krad.exception.ExportNotSupportedException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * An Exporter provides the ability to export a List of BusinessObjects to a
 * supported ExportFormat.
 * 
 * 
 */
public interface Exporter {

	/**
	 * Exports the List of BusinessObjects to the specified ExportFormat. The
	 * resulting output of the export operation should be written to the given
	 * OutputStream.
	 * 
	 * @param dataObjectClass
	 *            the type of DataObjects being exported
	 * @param data
	 *            a List of DataObjects to export
	 * @param exportFormat
	 *            the export format in which to export the DataObjects
	 * @param outputStream
	 *            the OutputStream to write the exported data to
	 * 
	 * @throws IOException
	 *             if the process encounters an I/O issue
	 * @throws ExportNotSupportedException
	 *             if the given ExportFormat is not supported
	 */
	public void export(Class<?> dataObjectClass,
			List<? extends Object> dataObjects, String exportFormat,
			OutputStream outputStream) throws IOException, ExportNotSupportedException;

	/**
	 * Returns a List of ExportFormats supported by this Exporter for the given
	 * DataOject class.
	 * 
	 * @param dataObjectClass
	 *            the class of the DataObjects being exported
	 * @return a List of supported ExportFormats
	 */
	public List<String> getSupportedFormats(Class<?> dataObjectClass);

}
