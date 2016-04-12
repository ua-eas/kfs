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
package org.kuali.kfs.coreservice.api.parameter;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = ParameterQueryResults.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ParameterQueryResults.Constants.TYPE_NAME, propOrder = {
		ParameterQueryResults.Elements.RESULTS,
		ParameterQueryResults.Elements.TOTAL_ROW_COUNT,
		ParameterQueryResults.Elements.MORE_RESULTS_AVAILALBE,
		CoreConstants.CommonElements.FUTURE_ELEMENTS })
public class ParameterQueryResults extends AbstractDataTransferObject implements QueryResults<Parameter> {

	@XmlElementWrapper(name = Elements.RESULTS, required = false)
	@XmlElement(name = Elements.PARAMETER, required = false)
	private final List<Parameter> results;
	
	@XmlElement(name = Elements.TOTAL_ROW_COUNT, required = false)
	private final Integer totalRowCount;
	
	@XmlElement(name = Elements.MORE_RESULTS_AVAILALBE, required = true)
	private final boolean moreResultsAvailable;
		
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;
	
	private ParameterQueryResults() {
		this.results = null;
		this.totalRowCount = null;
		this.moreResultsAvailable = false;
	}

	private ParameterQueryResults(Builder builder) {
		this.results = builder.getResults();
		this.totalRowCount = builder.getTotalRowCount();
		this.moreResultsAvailable = builder.isMoreResultsAvailable();
	}

	@Override
	public List<Parameter> getResults() {
		return results;
	}
	
	@Override
	public Integer getTotalRowCount() {
		return totalRowCount;
	}

	@Override
	public boolean isMoreResultsAvailable() {
		return moreResultsAvailable;
	}

	public static class Builder implements ModelBuilder, QueryResults<Parameter> {

		private List<Parameter> results;
		private Integer totalRowCount;
		private boolean moreResultsAvailable;

        public static Builder create() {
            return new Builder();
        }

		private Builder() {
			this.results = new ArrayList<Parameter>();
			this.moreResultsAvailable = false;
		}

        @Override
		public ParameterQueryResults build() {
			return new ParameterQueryResults(this);
		}

        @Override
		public List<Parameter> getResults() {
			return this.results;
		}

		public void setResults(List<Parameter> results) {
			this.results = results;
		}

        @Override
		public Integer getTotalRowCount() {
			return this.totalRowCount;
		}

		public void setTotalRowCount(Integer totalRowCount) {
			this.totalRowCount = totalRowCount;
		}

        @Override
		public boolean isMoreResultsAvailable() {
			return this.moreResultsAvailable;
		}

		public void setMoreResultsAvailable(boolean moreResultsAvailable) {
			this.moreResultsAvailable = moreResultsAvailable;
		}
		
	}
	
	/**
	 * Defines some internal constants used on this class.
	 */
	public static class Constants {
		public final static String ROOT_ELEMENT_NAME = "parameterQueryResults";
		public final static String TYPE_NAME = "ParameterQueryResultsType";
	}

	/**
	 * A private class which exposes constants which define the XML element
	 * names to use when this object is marshaled to XML.
	 */
	public static class Elements {
		public final static String RESULTS = "results";
		public final static String PARAMETER = "parameter";
		public final static String TOTAL_ROW_COUNT = "totalRowCount";
		public final static String MORE_RESULTS_AVAILALBE = "moreResultsAvailable";
	}
	
}
