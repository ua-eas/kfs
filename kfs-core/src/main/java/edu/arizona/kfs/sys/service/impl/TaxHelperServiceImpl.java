package edu.arizona.kfs.sys.service.impl;

import java.sql.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.service.TaxService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.sys.service.TaxHelperService;

public class TaxHelperServiceImpl implements TaxHelperService {

    TaxService taxService;

    public void setTaxService(TaxService taxService) {
        this.taxService = taxService;
    }

    @Override
    public KualiDecimal getTotalUseTaxAmount(Date dateOfTransaction, String postalCode, KualiDecimal amount) {
        KualiDecimal totalUseTaxAmount = KualiDecimal.ZERO;
        if (StringUtils.isBlank(postalCode)) {
            return totalUseTaxAmount; // return empty list if blank postalCode
        }

        List<TaxDetail> salesTaxDetails = taxService.getUseTaxDetails(dateOfTransaction, postalCode, amount);
        KualiDecimal newTaxAmount = KualiDecimal.ZERO;
        for (TaxDetail taxDetail : salesTaxDetails) {
            newTaxAmount = taxDetail.getTaxAmount();
            totalUseTaxAmount = totalUseTaxAmount.add(newTaxAmount);
        }

        return totalUseTaxAmount;
    }

}
