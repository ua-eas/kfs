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
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;


/**
 * This class is a direct copy of one that was in Kuali Student. Look up constraints are currently not implemented.
 *
 * @since 1.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LookupConstraint extends CommonLookup implements Constraint {


    private static final long serialVersionUID = 1L;
//	private String searchTypeId; // id of search type defined in search xml
//	private String resultReturnKey; // key of searchResultColumn to map back to
//									// this field
//	protected List<LookupConstraintParamMapping> lookupParams; // maps fields to
//																// search
//																// params?
//
//	public List<LookupConstraintParamMapping> getLookupParams() {
//		return lookupParams;
//	}
//
//	public void setLookupParams(List<LookupConstraintParamMapping> lookupParams) {
//		this.lookupParams = lookupParams;
//	}
}
