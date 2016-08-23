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
package org.kuali.kfs.krad.service.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl;
import org.kuali.kfs.krad.util.DateTimeConverter;
import org.kuali.kfs.krad.service.PersistenceService;

import java.util.ArrayList;

/**
 * This class is the service implementation for the XmlObjectSerializer structure. This is the default implementation that gets
 * delivered with Kuali. It utilizes the XStream open source libraries and framework.
 */
public class XmlObjectSerializerIgnoreMissingFieldsServiceImpl extends XmlObjectSerializerServiceImpl {
	private static final Log LOG = LogFactory.getLog(XmlObjectSerializerIgnoreMissingFieldsServiceImpl.class);

	private PersistenceService persistenceService;

	private XStream xstream;

	public XmlObjectSerializerIgnoreMissingFieldsServiceImpl() {

        xstream = new XStream(new ProxyAwareJavaReflectionProvider()) {
            @Override
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {
                    @Override
                    public boolean shouldSerializeMember(Class definedIn,
                            String fieldName) {
                        if (definedIn == Object.class) {
                            return false;
                        }
                      return super.shouldSerializeMember(definedIn, fieldName);
                   }
               };
           }
       };

		xstream.registerConverter(new ProxyConverter(xstream.getMapper(), xstream.getReflectionProvider() ));
		xstream.addDefaultImplementation(ArrayList.class, ListProxyDefaultImpl.class);
        xstream.registerConverter(new DateTimeConverter());
	}

    /**
     * @see org.kuali.rice.krad.service.XmlObjectSerializer#fromXml(java.lang.String)
     *
     *  Fields on the XML that do not exist on the class will be ignored.
     */
    public Object fromXml(String xml) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fromXml() : \n" + xml );
        }
        if ( xml != null ) {
            xml = xml.replaceAll( "--EnhancerByCGLIB--[0-9a-f]{0,8}", "" );
        }
        return xstream.fromXML(xml);
    }
}
