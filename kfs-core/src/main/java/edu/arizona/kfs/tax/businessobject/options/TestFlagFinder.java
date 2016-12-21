package edu.arizona.kfs.tax.businessobject.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class TestFlagFinder extends KeyValuesBase {

    public TestFlagFinder() {
       super();
       Map map = this.getKeyLabelMap();
       map.put("T", "T");
    }
    
    @Override
    public List getKeyValues() {
        List<ConcreteKeyValue> keyValuesList = new ArrayList<ConcreteKeyValue>();
        
        keyValuesList.add( new ConcreteKeyValue("T", "T") );
        
        return keyValuesList;
    }
}