package org.kuali.kfs.pdp.batch.service.impl;

import java.util.Collection;

import org.kuali.kfs.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.VelocityEmailService;
import org.kuali.kfs.sys.service.impl.VelocityEmailServiceBase;

public class FormatCheckACHEmailServiceImpl extends VelocityEmailServiceBase
		implements VelocityEmailService {

	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(FormatCheckACHEmailServiceImpl.class);

	private String templateUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kuali.kfs.sys.service.VelocityEmailService#getEmailSubject()
	 */
	@Override
	public String getEmailSubject() {
		return "Format Check/ACH summary for today.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.kuali.kfs.sys.service.impl.VelocityEmailServiceBase#getProdEmailReceivers
	 * ()
	 */
	@Override
	public Collection<String> getProdEmailReceivers() {
		Collection<String> emailReceivers = CoreFrameworkServiceLocator
				.getParameterService()
				.getParameterValuesAsString(
						KFSConstants.CoreModuleNamespaces.PDP,
						PdpConstants.FormatCheckACHParameters.PDP_FORMAT_CHECK_ACH_STEP,
						PdpConstants.FormatCheckACHParameters.FORMAT_SUMMARY_TO_EMAIL_ADDRESSES);
		return emailReceivers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kuali.kfs.sys.service.VelocityEmailService#getTemplateUrl()
	 */
	@Override
	public String getTemplateUrl() {
		return templateUrl;
	}

	/**
	 * @param templateUrl
	 *            the templateUrl to set
	 */
	public void setTemplateUrl(String templateUrl) {
		this.templateUrl = templateUrl;
	}

}
