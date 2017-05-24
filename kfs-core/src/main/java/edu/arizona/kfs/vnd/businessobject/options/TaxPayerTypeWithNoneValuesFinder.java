package edu.arizona.kfs.vnd.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * Generator of a list of tax payer type value pairs, including an empty one, as opposed to <code>TaxPayerTypeValuesFinder</code>,
 * which does not include an empty pair.
 * 
 * @see org.kuali.kfs.fp.businessobject.options.TaxPayerTypeValuesFinder
 */
public class TaxPayerTypeWithNoneValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue(VendorConstants.TAX_TYPE_FEIN, VendorConstants.TAX_TYPE_FEIN));
        keyValues.add(new ConcreteKeyValue(VendorConstants.TAX_TYPE_SSN, VendorConstants.TAX_TYPE_SSN));
        keyValues.add(new ConcreteKeyValue(VendorConstants.NONE, VendorConstants.NONE));
        return keyValues;
    }

}
