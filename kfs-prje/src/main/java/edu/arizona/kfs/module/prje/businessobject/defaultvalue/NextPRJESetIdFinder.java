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

import edu.arizona.kfs.module.prje.businessobject.PRJESet;


/**
 * A class which lets the PRJE set maintenance document pre-populate the ID for the PRJE set before routing
 */
public class NextPRJESetIdFinder extends NextSequenceIdFinder {

    /**
     * The name of PRJE Set's ID sequence, "PRJE_SET_ID_SEQ"
     * @see edu.arizona.kfs.module.prje.businessobject.defaultvalue.NextSequenceIdFinder#getSequenceName()
     */
    @Override
    public String getSequenceName() {
        return "PRJE_SET_ID_SEQ";
    }

    public Long getLongValue() {
        // no constant because this is the only place the sequence name is used
        SequenceAccessorService sas = KRADServiceLocator.getSequenceAccessorService();
        return sas.getNextAvailableSequenceNumber(getSequenceName(), PRJESet.class);
    }
    

}
