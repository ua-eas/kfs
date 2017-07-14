package edu.arizona.rice.krad.dao.impl;

import static org.kuali.kfs.sys.KFSPropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.gl.GeneralLedgerConstants;

public class GecLookupDaoOjb extends LookupDaoOjb {
    private ParameterService parameterService;
    private OptionsService optionsService;
    private BusinessObjectService businessObjectService;
    private String actualFinancialBalanceTypeCd;
    private Collection<String> restrictedOriginationCodes;
    private Collection<String> validDocumentTypeCodes;
    private Collection<String> restrictedObjectTypeCodes;
    private Collection<String> restrictedBankAccounts;


    @Override
    public Criteria getCollectionCriteriaFromMap(BusinessObject example, Map formProps) {
        Criteria criteria = super.getCollectionCriteriaFromMap(example, formProps);

        criteria.addNotIn(FINANCIAL_OBJECT_TYPE_CODE, getRestrictedObjectTypeCodes());
        criteria.addNotIn(ACCOUNT_NUMBER, getRestrictedBankAccounts());
        criteria.addNotIn(FINANCIAL_SYSTEM_ORIGINATION_CODE, getRestrictedOriginationCodes());
        criteria.addNotIn(FINANCIAL_DOCUMENT_TYPE_CODE, getValidDocumentTypeCodes());
        criteria.addLike(FINANCIAL_BALANCE_TYPE_CODE, getActualFinancialBalanceTypeCd());

        return criteria;
    }


    public String getActualFinancialBalanceTypeCd() {
        if (actualFinancialBalanceTypeCd == null) {
            actualFinancialBalanceTypeCd = getOptionsService().getCurrentYearOptions().getActualFinancialBalanceTypeCd();
        }
        return actualFinancialBalanceTypeCd;
    }


    public Collection<String> getRestrictedOriginationCodes() {
        if (restrictedOriginationCodes == null) {
            restrictedOriginationCodes = getParameterService().getParameterValuesAsString(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.ORIGIN_CODES);
        }
        return restrictedOriginationCodes;
    }


    public Collection<String> getValidDocumentTypeCodes() {
        if (validDocumentTypeCodes == null) {
            validDocumentTypeCodes = getParameterService().getParameterValuesAsString(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.DOCUMENT_TYPES);
        }
        return validDocumentTypeCodes;
    }


    public Collection<String> getRestrictedObjectTypeCodes() {
        if (restrictedObjectTypeCodes == null) {
            restrictedObjectTypeCodes = getParameterService().getParameterValuesAsString(GeneralErrorCorrectionDocument.class, GeneralLedgerConstants.GeneralErrorCorrectionGroupParameters.RESTRICTED_OBJECT_TYPE_CODES);
        }
        return restrictedObjectTypeCodes;
    }


    public Collection<String> getRestrictedBankAccounts() {
        if (restrictedBankAccounts == null) {
            restrictedBankAccounts = new ArrayList<String>();
            for (Bank bank : getBusinessObjectService().findAll(Bank.class)) {
                restrictedBankAccounts.add(bank.getBankAccountNumber());
            }
        }
        return restrictedBankAccounts;
    }


    private ParameterService getParameterService() {
        return parameterService;
    }


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    private OptionsService getOptionsService() {
        return optionsService;
    }


    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }


    private BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
