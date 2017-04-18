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

package edu.arizona.kfs.module.prje;

import edu.arizona.kfs.module.prje.lookup.KeyLabelFactory;

/**
 * PRJEConstants stores all of the constants in the PRJE module.  They
 * are being implemented using the KeyValueFactory interfaces so that a
 * simple introspection of this class can be used to produce key/label
 * pairs for the UI and data dictionaries.
 * 
 * @author tbradford
 */
public interface PRJEConstants {
    public static final String SOURCE_CODE = "PRJE";
    public static final String NAMESPACE = "KUALI-PRJE";
    public static final String COMPONENT = "ProrateJournalEntry";
    public static final String CONFIGURATION = "PRJE_CONFIGURATION";
    public static final String PROPERTIES = "properties";
    public static final String PARAMETERS = "parameters";
    public static final String OBJECT_TYPE = "PRJE_OBJECT_TYPE";
    public static final String TABLE_FISCAL_YEAR = "PRJE_TABLE_FISCAL_YEAR";
    public static final String TABLE_CURRENT_PERIOD = "PRJE_TABLE_CURRENT_PERIOD";
    public static final String PROCESS_ORDER = "PRJE_PROCESS_ORDER";
    public static final String SET_DOCTYPE = "PRJS";
    public static final String TYPE_DOCTYPE = "PRJT";
    public static final String ENTRY_DOCTYPE = "PRJE";
    public static final String ENTRY_ORIGIN = "MF";
    public static final String PRJE_AMOUNT = "A";
    public static final String PRJE_PERCENT = "P";

    enum Frequency implements KeyLabelFactory.KeyLabelConstant {
        MONTHLY ("M", "Monthly Amount"),
        YEARLY ("Y", "Yearly Amount");
        
        private final String key;
        private final String label;
        
        Frequency(String key, String label) {
            this.key = key;
            this.label = label;
        }
        
        public String getKey() { return key; }
        public String getLabel() { return label; }
        public String toString() { return key; }        
    }

    enum ProrateDebitType implements KeyLabelFactory.KeyLabelConstant {
        PERCENTAGE ("P", "Percentage"),
        AMOUNT ("A", "Amount");        
        
        private final String key;
        private final String label;
        
        ProrateDebitType(String key, String label) {
            this.key = key;
            this.label = label;
        }
        
        public String getKey() { return key; }
        public String getLabel() { return label; }
        public String toString() { return key; }        
    }    

    enum ProrateCreditType implements KeyLabelFactory.KeyLabelConstant {
        PERCENTAGE ("P", "Percentage"),
        AMOUNT ("A", "Amount"),
        NO_OVERRIDE ("0", "No Override");        
        
        private final String key;
        private final String label;
        
        ProrateCreditType(String key, String label) {
            this.key = key;
            this.label = label;
        }
        
        public String getKey() { return key; }
        public String getLabel() { return label; }
        public String toString() { return key; }        
    }    

    enum Containment implements KeyLabelFactory.KeyLabelConstant {
        INCLUDE ("I", "Include"),
        EXCLUDE ("X", "Exclude");
        
        private final String key;
        private final String label;
        
        Containment(String key, String label) {
            this.key = key;
            this.label = label;
        }

        public String getKey() { return key; }
        public String getLabel() { return label; }
        public String toString() { return key; }
    } 
    
    enum ProrateOptions implements KeyLabelFactory.KeyLabelConstant {
        SINGLE_TO_MULTIPLE("1", "Single to Multiple"),
        MULTIPLE_TO_SINGLE ("2", "Multiple to Single"),
        SINGLE_TO_SINGLE ("3", "Single");
        
        private final String key;
        private final String label;
        
        ProrateOptions(String key, String label) {
            this.key = key;
            this.label = label;
        }

        public String getKey() { return key; }
        public String getLabel() { return label; }
        public String toString() { return key; }        
    }
}
