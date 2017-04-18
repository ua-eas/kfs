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

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * KeyLabelFactory provides a KeyValues implementation for UI and data 
 * dictionary instances.  There's no reason to create a bunch of classes 
 * that just return key/label pairs when you can keep the constants and 
 * the pairs they produce in one place and then use reflection to gather
 * the information.
 * 
 * One's inclination might be to just define your constants in a subclass of
 * KeyLabelFactory, but we don't want to make any assumptions about the type 
 * of enumerations any of its superclasses will be exposing.  Therefore, it's 
 * advised to create an interface or standalone class and use that when 
 * instantiating the Factory.
 * 
 * @author tbradford
 * @todo refactor out into a better location
 */
public class KeyLabelFactory extends KeyValuesBase {
    
    @SuppressWarnings("unchecked")
    private Class constantsClass;
    private String enumName;
    
    @SuppressWarnings("unchecked")
    public KeyLabelFactory(Class enumClass) {
        super();
        this.constantsClass = enumClass.getEnclosingClass();
        this.enumName = enumClass.getSimpleName();
    }
    
    private KeyValue pair(String key, String label) {
        return new ConcreteKeyValue(key, label);
    }
    
    @SuppressWarnings("unchecked")
    public List<KeyValue> getKeyValues() {
        List<KeyValue> result = new ArrayList<KeyValue>();
        
        // The class passed in may not contain enumerations with elements
        // that implement our ConstantGroup interface, so check.
        
        Class[] members = constantsClass.getClasses();
        for ( Class member : members ) {
            if ( member.isEnum() ) {
                Object[] constants = member.getEnumConstants();
                     
                // It's perfectly legal for an enum to have no elements.
                
                if ( constants.length == 0 )
                    continue;

                // It's also very obviously legal to have a group of items 
                // that don't implement the KeyLabelConstant interface, so 
                // check to make sure.
                
                if ( !(constants[0] instanceof KeyLabelConstant ) )
                    continue;
                
                for ( KeyLabelConstant klc : (KeyLabelConstant[])constants ) {
                    if ( klc.getClass().getSimpleName().equals(enumName) )                 
                        result.add(pair(klc.getKey(), klc.getLabel()));
                    else
                        break;
                }
            }
        }
        
        return result;
    }
    
    public interface KeyLabelConstant {
        public String getKey();
        public String getLabel();
    }    
}
