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
package org.kuali.kfs.coreservice.impl.style;

import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.kuali.kfs.coreservice.api.style.Style;
import org.kuali.kfs.coreservice.api.style.StyleService;

/**
 * A URIResolver that knows how to resolve href's based on style names.
 *
 */
class StyleUriResolver implements URIResolver {

	private static final Logger LOG = Logger.getLogger(StyleUriResolver.class);

	private final StyleService styleService;

	StyleUriResolver(StyleService styleService) {
		if (styleService == null) {
			throw new IllegalArgumentException("styleService cannot be null");
		}
		this.styleService = styleService;
	}

	public Source resolve(String href, String base) {

		try {
			Style style = styleService.getStyle(href);
			return new StreamSource(new StringReader(style.getXmlContent()));

		} catch (Exception e) {
			LOG.error("Error ocurred getting style " + href, e);
		}
		return null;
	}

}
