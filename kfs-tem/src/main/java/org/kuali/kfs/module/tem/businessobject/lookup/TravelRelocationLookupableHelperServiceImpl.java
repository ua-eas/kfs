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
package org.kuali.kfs.module.tem.businessobject.lookup;

import org.kuali.kfs.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.UrlFactory;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.sys.KFSConstants;

import java.util.Properties;

@SuppressWarnings("deprecation")
public class TravelRelocationLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    @Override
    public String getSupplementalMenuBar() {
        String menuBar = super.getSupplementalMenuBar();

        Properties createParameters = new Properties();
        createParameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
        createParameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TravelRelocationDocument.class.getName());

        String createUrl = UrlFactory.parameterizeUrl(KRADConstants.MAINTENANCE_ACTION, createParameters);

        menuBar = "<a href=\"" + createUrl + "\"><span class=\"tinybutton btn btn-default\">Create New</span></a>&nbsp;" + menuBar;

        return menuBar;
    }

}
