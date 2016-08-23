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
package org.kuali.kfs.kns.web.ui;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.util.KRADConstants;

import java.io.Serializable;

/**
 * Represents an extra button that may appear on the lookups or bottom of a
 * document page.
 */
@Deprecated
public class ExtraButton implements Serializable {
	private String extraButtonSource = "";

	private String extraButtonAltText = "";

	private String extraButtonParams = "";

	private String extraButtonProperty = "";

	private String extraButtonOnclick = "";

	public String getExtraButtonAltText() {
		return extraButtonAltText;
	}

	public void setExtraButtonAltText(String extraButtonAltText) {
		this.extraButtonAltText = extraButtonAltText;
	}

	public String getExtraButtonParams() {
		return extraButtonParams;
	}

	public void setExtraButtonParams(String extraButtonParams) {
		this.extraButtonParams = extraButtonParams;
	}

	public String getExtraButtonProperty() {
		return extraButtonProperty;
	}

	public void setExtraButtonProperty(String extraButtonProperty) {
		this.extraButtonProperty = extraButtonProperty;
	}

	public String getExtraButtonSource() {
		return extraButtonSource;
	}

	public String getExtraButtonOnclick() {
	    return this.extraButtonOnclick;
	}

	public void setExtraButtonOnclick(String extraButtonOnclick) {
	    this.extraButtonOnclick = extraButtonOnclick;
	}

	public void setExtraButtonSource(String extraButtonSource) {
		if (StringUtils.isNotBlank(extraButtonSource)) {
			this.extraButtonSource = extraButtonSource
					.replace(
							"${kr.externalizable.images.url}",
							KRADServiceLocator
									.getKualiConfigurationService()
									.getPropertyValueAsString(KRADConstants.EXTERNALIZABLE_IMAGES_URL_KEY))
					.replace(
							"${externalizable.images.url}",
							KRADServiceLocator
									.getKualiConfigurationService()
									.getPropertyValueAsString(KRADConstants.APPLICATION_EXTERNALIZABLE_IMAGES_URL_KEY));
		}
	}

}
