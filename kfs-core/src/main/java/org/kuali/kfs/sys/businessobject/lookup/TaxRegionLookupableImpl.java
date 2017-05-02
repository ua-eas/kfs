/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.sys.businessobject.lookup;

import org.kuali.kfs.kns.lookup.KualiLookupableImpl;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.UrlFactory;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.util.Properties;

public class TaxRegionLookupableImpl extends KualiLookupableImpl {

    /**
     * Make create new action go to Tax Region Type lookup first. Adding the tax region from lookup indicator
     * (CREATE_TAX_REGION_FROM_LOOKUP_INDICATOR) to the conversion fields to control when you should do a regular tax region type
     * code lookup vs. one that actually creates a tax region.  This indicator is then used in
     * TaxRegionTypeLookupableImpl.
     *
     * @see org.kuali.core.lookup.KualiLookupableImpl#getCreateNewUrl()
     */
    @Override
    public String getCreateNewUrl() {
        String url = "";

        if (getLookupableHelperService().allowsMaintenanceNewOrCopyAction()) {
            Properties parameters = new Properties();
            parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TaxRegionType.class.getName());
            parameters.put(KRADConstants.RETURN_LOCATION_PARAMETER, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + KRADConstants.PORTAL_ACTION);
            parameters.put(KRADConstants.DOC_FORM_KEY, getTaxRegionDocumentTypeName());
            parameters.put(KRADConstants.CONVERSION_FIELDS_PARAMETER, "taxRegionTypeCode:taxRegionTypeCode");
            url = getCreateNewUrl(UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, parameters));
        }

        return url;
    }

    private String getTaxRegionDocumentTypeName() {
        return getDataDictionaryService().getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(TaxRegion.class).getDocumentTypeName();
    }

}
