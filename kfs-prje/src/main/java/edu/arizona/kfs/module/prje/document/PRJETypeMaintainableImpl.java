/*
 * Copyright 2011 The Kuali Foundation.
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
package edu.arizona.kfs.module.prje.document;

import java.util.Map;

import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;

import edu.arizona.kfs.module.prje.businessobject.PRJEType;
import edu.arizona.kfs.module.prje.businessobject.defaultvalue.NextPRJETypeIdFinder;

public class PRJETypeMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);
        // KFSI-3431 KITT-2335 derive the type ID
        setPRJETypeIdIfBlank();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterNew(document, parameters);
        // KFSI-3431 KITT-2335 derive the type ID
        setPRJETypeIdIfBlank();
    }
    
    protected void setPRJETypeIdIfBlank() {
        PRJEType prjeType = (PRJEType) getBusinessObject();
        if (prjeType.getTypeId() == null) {
            prjeType.setTypeId(Integer.valueOf(new NextPRJETypeIdFinder().getValue()));
        }
    }
}
