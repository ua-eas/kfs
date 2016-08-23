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
package org.kuali.kfs.kns.web.struts.form;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.kfs.kns.service.BusinessObjectAuthorizationService;
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.bo.BusinessObject;

import javax.servlet.http.HttpServletRequest;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - wliang don't forget to fill this in.
 *
 *
 *
 */
public class DisplayInactivationBlockersForm extends org.kuali.kfs.kns.web.struts.form.KualiForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisplayInactivationBlockersForm.class);

	private String businessObjectClassName;
	private Map<String, String> primaryKeyFieldValues;
	private Map<String, List<String>> blockingValues;

	@Override
	public void populate(HttpServletRequest request) {
		super.populate(request);
		primaryKeyFieldValues = new HashMap<String, String>();

		if (StringUtils.isBlank(businessObjectClassName)) {
			throw new IllegalArgumentException("BO Class Name missing.");
		}

		Class businessObjectClass = null;
		try {
			businessObjectClass = Class.forName(businessObjectClassName);
		} catch (ClassNotFoundException e) {
			LOG.error("Unable to find class: " + businessObjectClassName, e);
			throw new IllegalArgumentException("Unable to find class: " + businessObjectClassName, e);
		}

		if (!BusinessObject.class.isAssignableFrom(businessObjectClass)) {
			LOG.error("BO Class is not a BusinessObject: " + businessObjectClassName);
			throw new IllegalArgumentException("BO Class is not a BusinessObject: " + businessObjectClassName);
		}

		EncryptionService encryptionService = CoreApiServiceLocator.getEncryptionService();
		BusinessObjectAuthorizationService businessObjectAuthorizationService = KNSServiceLocator
                .getBusinessObjectAuthorizationService();

		List primaryKeyFieldNames = KNSServiceLocator.getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(businessObjectClass);
		for (Iterator i = primaryKeyFieldNames.iterator(); i.hasNext();) {
			String primaryKeyFieldName = (String) i.next();

			String primaryKeyFieldValue = request.getParameter(primaryKeyFieldName);
			if (StringUtils.isBlank(primaryKeyFieldValue)) {
				LOG.error("Missing primary key value for: " + primaryKeyFieldName);
				throw new IllegalArgumentException("Missing primary key value for: " + primaryKeyFieldName);
			}

            // check if field is a secure
            if (businessObjectAuthorizationService.attributeValueNeedsToBeEncryptedOnFormsAndLinks(businessObjectClass, primaryKeyFieldName)) {
                try {
                    if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                	    primaryKeyFieldValue = encryptionService.decrypt(primaryKeyFieldValue);
                    }
                }
                catch (GeneralSecurityException e) {
                    LOG.error("Unable to decrypt secure field for BO " + businessObjectClassName + " field " + primaryKeyFieldName, e);
                    throw new RuntimeException("Unable to decrypt secure field for BO " + businessObjectClassName + " field " + primaryKeyFieldName, e);
                }
            }

			primaryKeyFieldValues.put(primaryKeyFieldName, primaryKeyFieldValue);
		}
	}

	public String getBusinessObjectClassName() {
		return this.businessObjectClassName;
	}

	public void setBusinessObjectClassName(String businessObjectClassName) {
		this.businessObjectClassName = businessObjectClassName;
	}

	public Map<String, String> getPrimaryKeyFieldValues() {
		return this.primaryKeyFieldValues;
	}

	public void setPrimaryKeyFieldValues(Map<String, String> primaryKeyFieldValues) {
		this.primaryKeyFieldValues = primaryKeyFieldValues;
	}

	public Map<String, List<String>> getBlockingValues() {
		return this.blockingValues;
	}

	public void setBlockingValues(Map<String, List<String>> blockingValues) {
		this.blockingValues = blockingValues;
	}
}
