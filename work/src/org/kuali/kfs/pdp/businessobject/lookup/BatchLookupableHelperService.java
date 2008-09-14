/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.pdp.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.document.service.BatchMaintenanceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.format.BooleanFormatter;

public class BatchLookupableHelperService extends KualiLookupableHelperServiceImpl {
    private KualiConfigurationService configurationService;
    private BatchMaintenanceService batchMaintenanceService;

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> arg0) {
        Map parameters = super.getParameters();
        if (parameters.containsKey(PdpParameterConstants.BatchConstants.ACTION_SUCCESSFUL_PARAM)) {
            String[] actionSuccessRequestParm = (String[]) parameters.get(PdpParameterConstants.BatchConstants.ACTION_SUCCESSFUL_PARAM);
            Boolean actionSuccess = (Boolean) (new BooleanFormatter()).convertFromPresentationFormat(actionSuccessRequestParm[0]);

            if (actionSuccess != null) {
                if (parameters.containsKey(PdpParameterConstants.BatchConstants.MESSAGE_PARAM)) {
                    String[] messageRequestParm = (String[]) parameters.get(PdpParameterConstants.BatchConstants.MESSAGE_PARAM);
                    String message = messageRequestParm[0];
                    if (!actionSuccess) {
                        GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, message);
                    }
                    else {
                        GlobalVariables.getMessageList().add(message);
                    }
                }
            }
        }

        return super.getSearchResults(arg0);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        AnchorHtmlData inquiryUrl = (AnchorHtmlData)super.getInquiryUrl(bo, propertyName);
        Batch batch = (Batch) bo;
        if (propertyName.equalsIgnoreCase(PdpPropertyConstants.BatchConstants.Fields.BATCH_ID)) {
            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PaymentDetail.class.getName());
            params.put(KNSConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.RETURN_LOCATION_PARAMETER, KFSConstants.MAPPING_PORTAL + ".do");
            params.put(PdpPropertyConstants.PaymentDetail.Fields.PAYMENT_GROUP_BATCH_ID, UrlFactory.encode(String.valueOf(batch.getId())));
            String url = UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);
            inquiryUrl.setHref(url);
        }
        return inquiryUrl;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {

        if (businessObject instanceof Batch) {
            UniversalUser universalUser = GlobalVariables.getUserSession().getUniversalUser();
            Batch batch = (Batch) businessObject;
            Integer batchId = batch.getId();
            List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
            String linkText = KFSConstants.EMPTY_STRING;
            String url = KFSConstants.EMPTY_STRING;
            String basePath = configurationService.getPropertyString(KFSConstants.APPLICATION_URL_KEY) + "/" + PdpConstants.Actions.BATCH_SEARCH_DETAIL_ACTION;

            if (universalUser.isMember(PdpConstants.Groups.CANCEL_GROUP) && batchMaintenanceService.doBatchPaymentsHaveOpenOrHeldStatus(batchId)) {

                Properties params = new Properties();
                params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_CANCEL_ACTION);
                params.put(PdpParameterConstants.BatchConstants.BATCH_ID_PARAM, UrlFactory.encode(String.valueOf(batch.getId())));
                url = UrlFactory.parameterizeUrl(basePath, params);

                linkText = configurationService.getPropertyString(PdpKeyConstants.BatchConstants.LinkText.CANCEL_BATCH);

                AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_CANCEL_ACTION, linkText);
                anchorHtmlDataList.add(anchorHtmlData);
            }

            if (universalUser.isMember(PdpConstants.Groups.HOLD_GROUP)) {

                if (batchMaintenanceService.doBatchPaymentsHaveHeldStatus(batchId)) {

                    Properties params = new Properties();
                    params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_REMOVE_HOLD_ACTION);
                    params.put(PdpParameterConstants.BatchConstants.BATCH_ID_PARAM, UrlFactory.encode(String.valueOf(batch.getId())));
                    url = UrlFactory.parameterizeUrl(basePath, params);

                    linkText = configurationService.getPropertyString(PdpKeyConstants.BatchConstants.LinkText.REMOVE_BATCH_HOLD);

                    AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_REMOVE_HOLD_ACTION, linkText);
                    anchorHtmlDataList.add(anchorHtmlData);
                }
                else {

                    Properties params = new Properties();
                    params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, PdpConstants.ActionMethods.CONFIRM_HOLD_ACTION);
                    params.put(PdpParameterConstants.BatchConstants.BATCH_ID_PARAM, UrlFactory.encode(String.valueOf(batch.getId())));
                    url = UrlFactory.parameterizeUrl(basePath, params);

                    linkText = configurationService.getPropertyString(PdpKeyConstants.BatchConstants.LinkText.HOLD_BATCH);
                    AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, PdpConstants.ActionMethods.CONFIRM_HOLD_ACTION, linkText);
                    anchorHtmlDataList.add(anchorHtmlData);
                }
            }
            return anchorHtmlDataList;
        }
        return super.getEmptyActionUrls();
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        // call super method to check validation against DD
        super.validateSearchParameters(fieldValues);

        // get field values
        String batchIdValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.Fields.BATCH_ID);
        String paymentCountValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.Fields.PAYMENT_COUNT);
        String paymentTotalAmountValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.Fields.PAYMENT_TOTAL_AMOUNT);
        String fileCreationTimeValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.Fields.FILE_CREATION_TIME);
        String chartCodeValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.Fields.CHART_CODE);
        String orgCodeValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.Fields.ORG_CODE);
        String subUnitCodeValue = (String) fieldValues.get(PdpPropertyConstants.BatchConstants.Fields.SUB_UNIT_CODE);

        //check if there is any search criteria entered
        if (StringUtils.isEmpty(batchIdValue) && StringUtils.isEmpty(chartCodeValue) && StringUtils.isEmpty(orgCodeValue) && StringUtils.isEmpty(subUnitCodeValue) && StringUtils.isEmpty(paymentCountValue) && StringUtils.isEmpty(paymentTotalAmountValue) && StringUtils.isEmpty(fileCreationTimeValue)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_HEADER_ERRORS, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_BATCH_CRITERIA_NONE_ENTERED);
        }
        else if (StringUtils.isEmpty(batchIdValue) && StringUtils.isEmpty(paymentCountValue) && StringUtils.isEmpty(paymentTotalAmountValue)) {
            // If batchId, paymentCount, and paymentTotalAmount are empty then at least creation date is required
            if (StringUtils.isEmpty(fileCreationTimeValue)) {
                GlobalVariables.getErrorMap().putError(PdpPropertyConstants.BatchConstants.Fields.FILE_CREATION_TIME, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_BATCH_CRITERIA_NO_DATE);

            }
            else if (StringUtils.isNotEmpty(fileCreationTimeValue) && !StringUtils.contains(fileCreationTimeValue, "..")) {
                // If we have one (but not both) dates the user must enter either the chartCode, orgCode, or subUnitCode
                if (StringUtils.isEmpty(chartCodeValue) && StringUtils.isEmpty(orgCodeValue) && StringUtils.isEmpty(subUnitCodeValue)) {
                    GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_BATCH_CRITERIA_SOURCE_MISSING);
                }
            }
        }

        if (!GlobalVariables.getErrorMap().isEmpty()) {
            throw new ValidationException("errors in search criteria");
        }
    }

    public KualiConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public BatchMaintenanceService getBatchMaintenanceService() {
        return batchMaintenanceService;
    }

    public void setBatchMaintenanceService(BatchMaintenanceService batchMaintenanceService) {
        this.batchMaintenanceService = batchMaintenanceService;
    }

}
