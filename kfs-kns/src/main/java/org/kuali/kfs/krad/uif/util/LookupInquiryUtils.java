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
package org.kuali.kfs.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.web.form.UifFormBase;

import javax.servlet.http.HttpServletRequest;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * Class for utility methods that pertain to UIF Lookup processing
 * 
 * 
 */
public class LookupInquiryUtils {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupInquiryUtils.class);

	public static String retrieveLookupParameterValue(UifFormBase form, HttpServletRequest request,
			Class<?> lookupObjectClass, String propertyName, String propertyValueName) {
		String parameterValue = "";

		// get literal parameter values first
		if (StringUtils.startsWith(propertyValueName, "'") && StringUtils.endsWith(propertyValueName, "'")) {
            parameterValue = StringUtils.removeStart(propertyValueName, "'");
			parameterValue = StringUtils.removeEnd(propertyValueName, "'");
		}
		else if (parameterValue.startsWith(KRADConstants.LOOKUP_PARAMETER_LITERAL_PREFIX
				+ KRADConstants.LOOKUP_PARAMETER_LITERAL_DELIMITER)) {
			parameterValue = StringUtils.removeStart(parameterValue, KRADConstants.LOOKUP_PARAMETER_LITERAL_PREFIX
					+ KRADConstants.LOOKUP_PARAMETER_LITERAL_DELIMITER);
		}
		// check if parameter is in request
		else if (request.getParameterMap().containsKey(propertyValueName)) {
			parameterValue = request.getParameter(propertyValueName);
		}
		// get parameter value from form object
		else {
			Object value = ObjectPropertyUtils.getPropertyValue(form, propertyValueName);
			if (value != null) {
				if (value instanceof String) {
					parameterValue = (String) value;
				}

				Formatter formatter = Formatter.getFormatter(value.getClass());
				parameterValue = (String) formatter.format(value);
			}
		}

		if (parameterValue != null
				&& lookupObjectClass != null
				&& KRADServiceLocatorWeb.getDataObjectAuthorizationService()
						.attributeValueNeedsToBeEncryptedOnFormsAndLinks(lookupObjectClass, propertyName)) {
			try {
                if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
				    parameterValue = CoreApiServiceLocator.getEncryptionService().encrypt(parameterValue)
					    	+ EncryptionService.ENCRYPTION_POST_PREFIX;
                }
			}
			catch (GeneralSecurityException e) {
				LOG.error("Unable to encrypt value for property name: " + propertyName);
				throw new RuntimeException(e);
			}
		}

		return parameterValue;
	}

    public static String getBaseLookupUrl() {
        return KRADServiceLocator.getKualiConfigurationService().
                getPropertyValueAsString(KRADConstants.KRAD_LOOKUP_URL_KEY);
    }

    /**
	 * Helper method for building the title text for an element and a map of
	 * key/value pairs
	 * 
	 * @param prependText
	 *            - text to prepend to the title
	 * @param element
	 *            - element class the title is being generated for, used to as
	 *            the parent for getting the key labels
	 * @param keyValueMap
	 *            - map of key value pairs to add to the title text
	 * @return String title text
	 */
	public static String getLinkTitleText(String prependText, Class<?> element, Map<String, String> keyValueMap) {
		StringBuffer titleText = new StringBuffer(prependText);
		for (String key : keyValueMap.keySet()) {
			String fieldVal = keyValueMap.get(key).toString();

			titleText.append(KRADServiceLocatorWeb.getDataDictionaryService().getAttributeLabel(element, key) + "="
					+ fieldVal.toString() + " ");
		}

		return titleText.toString();
	}

}
