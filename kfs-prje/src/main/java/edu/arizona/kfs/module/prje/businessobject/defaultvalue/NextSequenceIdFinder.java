/*
 * Copyright 2010 The Kuali Foundation.
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
package edu.arizona.kfs.module.prje.businessobject.defaultvalue;

import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

import edu.arizona.kfs.module.prje.businessobject.PRJEType;

/**
 * An abstract sequence id finder
 */
public abstract class NextSequenceIdFinder implements ValueFinder {

    /**
     * Returns the value of the next ID in the sequence associated with the concrete class
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return getLongValue().toString();
    }
    
    /**
     * @return the next sequence number value as a Long.
     */
    public Long getLongValue() {
        // no constant because this is the only place the sequence name is used
        SequenceAccessorService sas = KRADServiceLocator.getSequenceAccessorService();
        return sas.getNextAvailableSequenceNumber("PRJE_TYPE_ID_SEQ", PRJEType.class);
    }
    
    /**
     * @return The name of the sequence to get the next value from
     */
    public abstract String getSequenceName();

}
