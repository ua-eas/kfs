/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.sys.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.SalesTax;
import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.service.TaxRegionService;
import org.kuali.kfs.sys.service.TaxService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TaxServiceImpl implements TaxService {

    protected static final String POSTAL_CODE_DIGITS_PASSED_TO_SALES_TAX_REGION_SERVICE = "POSTAL_CODE_DIGITS_PASSED_TO_SALES_TAX_REGION_SERVICE";

    protected TaxRegionService taxRegionService;
    protected ParameterService parameterService;

    /**
     * @see org.kuali.kfs.sys.service.TaxService#getSalesTaxDetails(java.lang.String, java.lang.String,
     *      org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public List<TaxDetail> getSalesTaxDetails(Date dateOfTransaction, String postalCode, KualiDecimal amount) {
        List<TaxDetail> salesTaxDetails = new ArrayList<TaxDetail>();

        if (StringUtils.isNotEmpty(postalCode)) {
            List<TaxRegion> salesTaxRegions = taxRegionService.getSalesTaxRegions(postalCode);
            TaxDetail newTaxDetail = null;
            for (TaxRegion taxRegion : salesTaxRegions) {
                if (taxRegion.isActive()) {
                    newTaxDetail = populateTaxDetail(taxRegion, dateOfTransaction, amount);
                    salesTaxDetails.add(newTaxDetail);
                }
            }
        }

        return salesTaxDetails;
    }

    /**
     * @see org.kuali.kfs.sys.service.TaxService#getUseTaxDetails(java.lang.String, java.lang.String,
     *      org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public List<TaxDetail> getUseTaxDetails(Date dateOfTransaction, String postalCode, KualiDecimal amount) {
        List<TaxDetail> useTaxDetails = new ArrayList<TaxDetail>();

        if (StringUtils.isNotEmpty(postalCode)) {
            //  strip digits from the postal code before passing it to the sales tax regions
            // if the parameters indicate to do so.
            postalCode = truncatePostalCodeForSalesTaxRegionService(postalCode);

            for (TaxRegion taxRegion : taxRegionService.getUseTaxRegions(postalCode)) {
                useTaxDetails.add(populateTaxDetail(taxRegion, dateOfTransaction, amount));
            }
        }

        return useTaxDetails;
    }

    /**
     * @see org.kuali.kfs.sys.service.TaxService#getTotalSalesTaxAmount(java.lang.String, java.lang.String,
     *      org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    @Override
    public KualiDecimal getTotalSalesTaxAmount(Date dateOfTransaction, String postalCode, KualiDecimal amount) {
        KualiDecimal totalSalesTaxAmount = KualiDecimal.ZERO;

        if (StringUtils.isNotEmpty(postalCode)) {

            //  strip digits from the postal code before passing it to the sales tax regions
            // if the parameters indicate to do so.
            postalCode = truncatePostalCodeForSalesTaxRegionService(postalCode);

            List<TaxDetail> salesTaxDetails = getSalesTaxDetails(dateOfTransaction, postalCode, amount);
            KualiDecimal newTaxAmount = KualiDecimal.ZERO;
            for (TaxDetail taxDetail : salesTaxDetails) {
                newTaxAmount = taxDetail.getTaxAmount();
                totalSalesTaxAmount = totalSalesTaxAmount.add(newTaxAmount);
            }
        }

        return totalSalesTaxAmount;
    }

    /**
     * This method returns a preTax amount
     *
     * @param dateOfTransaction
     * @param postalCode
     * @param amountWithTax
     * @return
     */

    @Override
    public KualiDecimal getPretaxAmount(Date dateOfTransaction, String postalCode, KualiDecimal amountWithTax) {
        BigDecimal totalTaxRate = BigDecimal.ZERO;

        // there is not tax amount
        if (StringUtils.isEmpty(postalCode))
            return amountWithTax;

        //  strip digits from the postal code before passing it to the sales tax regions
        // if the parameters indicate to do so.
        postalCode = truncatePostalCodeForSalesTaxRegionService(postalCode);

        List<TaxRegion> salesTaxRegions = taxRegionService.getSalesTaxRegions(postalCode);
        if (salesTaxRegions.size() == 0)
            return amountWithTax;

        for (TaxRegion taxRegion : salesTaxRegions) {
            if (ObjectUtils.isNotNull((taxRegion.getEffectiveTaxRegionRate(dateOfTransaction))))
                totalTaxRate = totalTaxRate.add(taxRegion.getEffectiveTaxRegionRate(dateOfTransaction).getTaxRate());
        }

        KualiDecimal divisor = new KualiDecimal(totalTaxRate.add(BigDecimal.ONE));
        KualiDecimal pretaxAmount = amountWithTax.divide(divisor);

        return pretaxAmount;
    }

    /**
     * This method returns a populated Tax Detail BO based on the Tax Region BO and amount
     *
     * @param taxRegion
     * @param amount
     * @return
     */
    protected TaxDetail populateTaxDetail(TaxRegion taxRegion, Date dateOfTransaction, KualiDecimal amount) {
        TaxDetail taxDetail = new TaxDetail();
        taxDetail.setAccountNumber(taxRegion.getAccountNumber());
        taxDetail.setChartOfAccountsCode(taxRegion.getChartOfAccountsCode());
        taxDetail.setFinancialObjectCode(taxRegion.getFinancialObjectCode());
        taxDetail.setRateCode(taxRegion.getTaxRegionCode());
        taxDetail.setRateName(taxRegion.getTaxRegionName());
        taxDetail.setTypeCode(taxRegion.getTaxRegionTypeCode());
        if (ObjectUtils.isNotNull((taxRegion.getEffectiveTaxRegionRate(dateOfTransaction)))) {
            taxDetail.setTaxRate(taxRegion.getEffectiveTaxRegionRate(dateOfTransaction).getTaxRate());
            if (amount != null) {
                taxDetail.setTaxAmount(new KualiDecimal((amount.bigDecimalValue().multiply(taxDetail.getTaxRate())).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR)));
            }
        }
        return taxDetail;
    }

    protected String truncatePostalCodeForSalesTaxRegionService(String postalCode) {
        Integer digitsToUse = postalCodeDigitsToUse();
        if (digitsToUse != null) {
            return postalCode.substring(0, digitsToUse.intValue());
        } else {
            return postalCode; // unchanged
        }
    }

    protected Integer postalCodeDigitsToUse() {
        String digits = parameterService.getParameterValueAsString(SalesTax.class,
                POSTAL_CODE_DIGITS_PASSED_TO_SALES_TAX_REGION_SERVICE);
        if (StringUtils.isBlank(digits)) { return null; }
        Integer digitsToUse;
        try {
            digitsToUse = new Integer(digits);
        }
        catch (NumberFormatException ex) {
            throw new RuntimeException("The value returned for Parameter " + POSTAL_CODE_DIGITS_PASSED_TO_SALES_TAX_REGION_SERVICE + " was non-numeric and cannot be processed.", ex);
        }
        return digitsToUse;
    }

    public void setTaxRegionService(TaxRegionService taxRegionService) {
        this.taxRegionService = taxRegionService;
    }
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
