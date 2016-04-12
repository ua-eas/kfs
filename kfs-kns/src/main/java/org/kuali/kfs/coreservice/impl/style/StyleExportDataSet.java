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
package org.kuali.kfs.coreservice.impl.style;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.kfs.coreservice.impl.style.StyleBo;

/**
 * A utility class for managing an {@link ExportDataSet} containing StyleBo
 * data.  Provides a mechanism to convert instances of this class to a
 * populated {@link ExportDataSet}.
 * 
 * @see ExportDataSet
 * @see StyleBo
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StyleExportDataSet {

	public static final QName STYLES = new QName("CORE", "styles");
	
	private List<StyleBo> styles = new ArrayList<StyleBo>();

	public List<StyleBo> getStyles() {
		return styles;
	}
	
	/**
	 * Populates the given {@link ExportDataSet} with the data from this data set.
	 * 
	 * @param exportDataSet the data set to populate the data into
	 */
	public void populateExportDataSet(ExportDataSet exportDataSet) {
		if (styles != null && !styles.isEmpty()) {
			exportDataSet.addDataSet(STYLES, styles);
		}
	}
	
	/**
	 * Converts this data set to a standard {@link ExportDataSet}, populating
	 * it with the data from this data set.
	 * 
	 * @return the populated ExportDataSet
	 */
	public ExportDataSet createExportDataSet() {
		ExportDataSet exportDataSet = new ExportDataSet();
		populateExportDataSet(exportDataSet);
		return exportDataSet;
	}
	
	/**
	 * A static utility for creating a {@link StyleExportDataSet} from an
	 * {@link ExportDataSet}.  This method will only populate the returned
	 * style data set with style data from the given export data set.  The
	 * rest of the data in the given export data set will be ignored.
	 * 
	 * @param exportDataSet the ExportDataSet to pull style data from
	 * @return a StyleExportDataSet with any style data from the given exportDataSet populated
	 */
	public static StyleExportDataSet fromExportDataSet(ExportDataSet exportDataSet) {
		StyleExportDataSet coreExportDataSet = new StyleExportDataSet();
		
		List<StyleBo> styles = (List<StyleBo>)exportDataSet.getDataSets().get(STYLES);
		if (styles != null) {
			coreExportDataSet.getStyles().addAll(styles);
		}
		
		return coreExportDataSet;
	}
	
}
