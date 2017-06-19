package edu.arizona.kfs.sys.service;

import java.sql.Date;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class provides supplementary methods for the delivered TaxService. Problems arose when putting these methods in an edu.arizona child class for TaxService.
 */

public interface TaxHelperService {
    /**
     * This method returns the total use tax amount
     *
     * @param dateOfTransaction
     *            date to include tax rates from
     * @param postalCode
     *            postal code to get tax rates
     * @param amount
     *            amount to be taxed
     * @return
     */
    KualiDecimal getTotalUseTaxAmount(Date dateOfTransaction, String postalCode, KualiDecimal amount);

}
