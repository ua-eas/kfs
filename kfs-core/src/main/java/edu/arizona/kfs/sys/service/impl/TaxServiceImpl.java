package edu.arizona.kfs.sys.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.service.TaxService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TaxServiceImpl extends org.kuali.kfs.sys.service.impl.TaxServiceImpl implements TaxService {

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

    @Override
    public List<TaxDetail> getUseTaxDetails(Date dateOfTransaction, String postalCode, KualiDecimal amount) {
        List<TaxDetail> useTaxDetails = new ArrayList<TaxDetail>();

        if (StringUtils.isNotEmpty(postalCode)) {
            // strip digits from the postal code before passing it to the sales tax regions
            // if the parameters indicate to do so.
            postalCode = truncatePostalCodeForSalesTaxRegionService(postalCode);

            for (TaxRegion taxRegion : taxRegionService.getUseTaxRegions(postalCode)) {
                useTaxDetails.add(populateTaxDetail(taxRegion, dateOfTransaction, amount));
            }
        }

        return useTaxDetails;
    }

    @Override
    public KualiDecimal getTotalSalesTaxAmount(Date dateOfTransaction, String postalCode, KualiDecimal amount) {
        KualiDecimal totalSalesTaxAmount = KualiDecimal.ZERO;

        if (StringUtils.isNotEmpty(postalCode)) {

            // strip digits from the postal code before passing it to the sales tax regions
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

}
