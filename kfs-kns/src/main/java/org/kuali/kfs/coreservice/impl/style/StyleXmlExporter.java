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

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.core.api.util.xml.XmlException;
import org.kuali.rice.core.api.util.xml.XmlHelper;
import org.kuali.rice.core.api.util.xml.XmlRenderer;
import org.kuali.rice.core.framework.impex.xml.XmlExporter;
import org.kuali.kfs.coreservice.impl.style.StyleBo;

import java.io.StringReader;
import java.util.Iterator;

import static org.kuali.rice.core.api.impex.xml.XmlConstants.*;

/**
 * Exports Style definitions to XML.
 *
 * @see org.kuali.rice.edl.impl.service.StyleService
 * @see org.kuali.rice.kew.StyleXmlParserImpl.StyleXmlParser
 * @see EDocLiteStyle
 */
public class StyleXmlExporter implements XmlExporter {
	private static final Logger LOG = Logger.getLogger(StyleXmlExporter.class);

	private XmlRenderer renderer = new XmlRenderer(STYLE_NAMESPACE);

	@Override
	public boolean supportPrettyPrint() {
		return false;
	}

	public Element export(ExportDataSet exportDataSet) {
		StyleExportDataSet dataSet = StyleExportDataSet.fromExportDataSet(exportDataSet);
		if (!dataSet.getStyles().isEmpty()) {
			Element rootElement = renderer.renderElement(null, STYLE_STYLES);
			rootElement.setAttribute(SCHEMA_LOCATION_ATTR, STYLE_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
			for (Iterator<StyleBo> iter = dataSet.getStyles().iterator(); iter.hasNext();) {
				StyleBo edocLite = iter.next();
				exportStyle(rootElement, edocLite);
			}
			return rootElement;
		}
		return null;
	}

	private void exportStyle(Element parentEl, StyleBo style) {
        if (style == null) {
            LOG.error("Attempted to export style which was not found");
            return;
        }

        Element styleWrapperEl = renderer.renderElement(parentEl, STYLE_STYLE);
        renderer.renderAttribute(styleWrapperEl, "name", style.getName());

        try {
            Element styleEl = XmlHelper.buildJDocument(new StringReader(style.getXmlContent())).getRootElement();
            styleWrapperEl.addContent(styleEl.detach());
		} catch (XmlException e) {
			throw new RuntimeException("Error building JDom document for style", e);
		}
	}
}
