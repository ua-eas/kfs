package edu.arizona.kfs.sys.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Note: TaxHelperService was created to add additional methods that caused problems with adding them to the TaxService interface.
 */
public class TaxServiceImpl extends org.kuali.kfs.sys.service.impl.TaxServiceImpl {

    @Override
    public List<TaxDetail> getSalesTaxDetails(Date dateOfTransaction, String postalCode, KualiDecimal amount) {
        List<TaxDetail> salesTaxDetails = new ArrayList<TaxDetail>();

        if (StringUtils.isNotEmpty(postalCode)) {
            postalCode = truncatePostalCodeForSalesTaxRegionService(postalCode);
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

}
