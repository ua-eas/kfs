package edu.arizona.kfs.fp.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

/**
 * This class returns list containing S = Suspended, T = Terminated, R = Reissue, and F = Transferred 
 */
public class ProcurementCardCancelValuesFinder extends KeyValuesBase {


    @Override
    @SuppressWarnings("rawtypes")
    public List getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue("B", "Canceled - Bank Request"));
        keyValues.add(new ConcreteKeyValue("C", "Canceled - Collections"));
        keyValues.add(new ConcreteKeyValue("E", "Canceled"));
        keyValues.add(new ConcreteKeyValue("I", "Canceled - Institution Specific"));
        keyValues.add(new ConcreteKeyValue("P", "Canceled - Temporary"));
        keyValues.add(new ConcreteKeyValue("M", "Canceled - Permanent"));
        keyValues.add(new ConcreteKeyValue("T", "Canceled - Cardholder Terminated"));
        keyValues.add(new ConcreteKeyValue("N", "Canceled - No Longer Needed"));
        keyValues.add(new ConcreteKeyValue("R", "Canceled - Customer Request"));
        keyValues.add(new ConcreteKeyValue("F", "Transferred"));
        keyValues.add(new ConcreteKeyValue("L", "Lost/Stolen"));
        keyValues.add(new ConcreteKeyValue("D", "Lost/Stolen Fraud"));
        keyValues.add(new ConcreteKeyValue("A", "Canceled - PA Requests Close"));
        keyValues.add(new ConcreteKeyValue("O", "Canceled Within Five Days of Opening"));
        keyValues.add(new ConcreteKeyValue("X", " "));
        return keyValues;
    }

}
