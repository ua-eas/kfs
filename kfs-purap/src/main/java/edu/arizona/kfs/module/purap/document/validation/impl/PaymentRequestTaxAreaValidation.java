package edu.arizona.kfs.module.purap.document.validation.impl;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.MessageMap;

public class PaymentRequestTaxAreaValidation extends org.kuali.kfs.module.purap.document.validation.impl.PaymentRequestTaxAreaValidation {

	/**
     * @see org.kuali.kfs.module.purap.document.validation.impl.PaymentRequestTaxAreaValidation#validateTaxRates(org.kuali.kfs.module.purap.document.PaymentRequestDocument)
     */
    @Override
    protected boolean validateTaxRates(PaymentRequestDocument preq) {
        boolean valid = true;
        String code = preq.getTaxClassificationCode();
        BigDecimal fedrate = preq.getTaxFederalPercent();
        BigDecimal strate = preq.getTaxStatePercent();
        MessageMap errorMap = GlobalVariables.getMessageMap();

        // only test the cases when income class and tax rates aren't empty/N
        if (StringUtils.isEmpty(code) || StringUtils.equalsIgnoreCase(code, DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_NON_REPORTABLE) || fedrate == null || strate == null) {
            return true;
        }

        // validate that the federal and state tax rates are among the allowed set
        ArrayList<BigDecimal> fedrates = super.retrieveTaxRates(code, DisbursementVoucherConstants.FEDERAL_TAX_TYPE_CODE); //(ArrayList<BigDecimal>) federalTaxRates.get(code);
        if (!super.listContainsValue(fedrates, fedrate)) {
            valid = false;
            errorMap.putError(PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_FEDERAL_PERCENT);
        }
        ArrayList<BigDecimal> strates = super.retrieveTaxRates(code, DisbursementVoucherConstants.STATE_TAX_TYPE_CODE); //(ArrayList<BigDecimal>) stateTaxRates.get(code);
        if (!super.listContainsValue(strates, strate)) {
            valid = false;
            errorMap.putError(PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF, PurapPropertyConstants.TAX_CLASSIFICATION_CODE, PurapPropertyConstants.TAX_STATE_PERCENT);
        }

        // validate that the federal and state tax rate abide to certain relationship
        if (fedrate.compareTo(new BigDecimal(0)) == 0 && strate.compareTo(new BigDecimal(0)) != 0) {
            valid = false;
            errorMap.putError(PurapPropertyConstants.TAX_STATE_PERCENT, PurapKeyConstants.ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF, PurapPropertyConstants.TAX_FEDERAL_PERCENT, PurapPropertyConstants.TAX_STATE_PERCENT);
        }       

        return valid;
    }
  
}
