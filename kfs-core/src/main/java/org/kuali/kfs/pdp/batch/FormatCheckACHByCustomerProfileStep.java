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
package org.kuali.kfs.pdp.batch;

import java.util.Date;

import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.batch.service.AutoCheckFormatService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractStep;

public class FormatCheckACHByCustomerProfileStep extends AbstractStep {

	private AutoCheckFormatService autoCheckFormatService;
	private ParameterService parameterService;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kuali.kfs.kns.bo.Step#execute(java.lang.String, java.util.Date)
	 */
	@Override
	public boolean execute(String jobName, Date jobRunDate)
			throws InterruptedException {

		// checks selected will be based on customer profile set in parameter.
		// If value is null, then ALL customer profiles will be selected.
		String customerProfileId = getParameterService()
				.getParameterValueAsString(
						KFSConstants.CoreModuleNamespaces.PDP,
						PdpConstants.FormatCheckACHParameters.PDP_FORMAT_CHECK_ACH_BY_CUST_PROF_STEP,
						PdpConstants.FORMAT_CUSTOMER_PROFILE_ID);

		return autoCheckFormatService
				.processChecksByCustomerProfile(customerProfileId);
	}

	/**
	 * @return
	 */
	public AutoCheckFormatService getAutoCheckFormatService() {
		return autoCheckFormatService;
	}

	/**
	 * @param autoCheckFormatService
	 */
	public void setAutoCheckFormatService(
			AutoCheckFormatService autoCheckFormatService) {
		this.autoCheckFormatService = autoCheckFormatService;
	}

	/**
	 * @return the parameterService
	 */
	public ParameterService getParameterService() {
		return parameterService;
	}

	/**
	 * @param parameterService
	 *            the parameterService to set
	 */
	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

}
