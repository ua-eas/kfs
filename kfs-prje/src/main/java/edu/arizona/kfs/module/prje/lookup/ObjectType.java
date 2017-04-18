/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kfs.module.prje.lookup;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import edu.arizona.kfs.module.prje.PRJEConstants;
import edu.arizona.kfs.module.prje.ProrateJournalEntry;

/** 
 * Return the list of Object Types listed in the PRJE_OBJECT_TYPE parameter
 */
public class ObjectType extends KeyValuesBase {
    @SuppressWarnings("unchecked")
    public List<KeyValue> getKeyValues() {        
        List<KeyValue> result = new ArrayList<KeyValue>();

        ParameterService ps = SpringContext.getBean(ParameterService.class);
        String objectType = ps.getParameterValueAsString(ProrateJournalEntry.class, 
                PRJEConstants.OBJECT_TYPE);
        
        String[] parms = objectType.toUpperCase().split("^[A-Z0-9]");
        for ( String parm : parms ) {
            result.add(new ConcreteKeyValue(parm, parm));
        }
        
        return result;
    }
}
