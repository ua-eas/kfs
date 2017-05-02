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
package org.kuali.kfs.module.ar.businessobject.inquiry;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.inquiry.KualiInquirableImpl;
import org.kuali.kfs.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.kns.web.ui.Row;
import org.kuali.kfs.kns.web.ui.Section;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.UrlFactory;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail;
import org.kuali.kfs.module.ar.service.CustomerViewService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CustomerInquirableImpl extends KualiInquirableImpl {

    private static volatile CustomerViewService customerViewService;

    /**
     * Overridden to hide CGB fields/sections if CGB is disabled.
     *
     * @see org.kuali.kfs.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.krad.bo.BusinessObject)
     */
    @Override
    public List<Section> getSections(BusinessObject businessObject) {
        List<Section> sections = super.getSections(businessObject);

        sections = getCustomerViewService().getSections(sections);

        return sections;
    }

    /**
     * Show the Customer Open Item Report tab. This is Customer History Report.
     *
     * @see org.kuali.kfs.kns.inquiry.KualiInquirableImpl#addAdditionalSections(java.util.List, org.kuali.rice.krad.bo.BusinessObject)
     * <p>
     * KRAD Conversion: Inquirable performs adding a new field and adding a new section to the sections.
     * <p>
     * Fields are in data dictionary for Customer.
     */
    @Override
    public void addAdditionalSections(List sections, BusinessObject bo) {
        if (bo instanceof Customer) {
            Customer customer = (Customer) bo;

            List rows = new ArrayList();

            Field f = new Field();
            f.setBusinessObjectClassName(bo.getClass().getName());
            f.setPropertyName(KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME);
            f.setFieldLabel("History Report");
            f.setPropertyValue("Click here to view the history report for this customer.");
            f.setFieldType(Field.TEXT);

            AnchorHtmlData inquiryHref = new AnchorHtmlData(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING, "view open item report");
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, CustomerOpenItemReportDetail.class.getName());
            parameters.put(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, ArConstants.CUSTOMER_OPEN_ITEM_REPORT_LOOKUPABLE_IMPL);
            parameters.put(KFSConstants.DOC_FORM_KEY, "88888888");
            parameters.put(KFSConstants.RETURN_LOCATION_PARAMETER, StringUtils.EMPTY);
            parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            parameters.put(KFSConstants.CustomerOpenItemReport.REPORT_NAME, KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME);
            parameters.put(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, customer.getCustomerNumber());
            parameters.put(ArPropertyConstants.CustomerFields.CUSTOMER_NAME, customer.getCustomerName());

            inquiryHref.setHref(UrlFactory.parameterizeUrl(getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + ArConstants.UrlActions.CUSTOMER_OPEN_ITEM_REPORT_LOOKUP, parameters));

            f.setInquiryURL(inquiryHref);
            rows.add(new Row(f));

            Section section = new Section();
            section.setRows(rows);
            section.setSectionTitle(KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME);
            sections.add(section);
        }
    }

    public static CustomerViewService getCustomerViewService() {
        if (customerViewService == null) {
            customerViewService = SpringContext.getBean(CustomerViewService.class);
        }
        return customerViewService;
    }

}
