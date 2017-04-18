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


/**
 * A class which lets the PRJE type maintenance document pre-populate the ID for the PRJE type before routing
 */
public class NextPRJETypeIdFinder extends NextSequenceIdFinder {

    /**
     * The name of PRJE Type's sequence, PRJE_TYPE_ID_SEQ
     * @see edu.arizona.kfs.prje.businessobject.defaultvalue.NextSequenceIdFinder#getSequenceName()
     */
    @Override
    public String getSequenceName() {
        return "PRJE_TYPE_ID_SEQ";
    }

    
}
