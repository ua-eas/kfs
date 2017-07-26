package edu.arizona.kfs.module.prje.document;

import java.util.Map;

import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.kns.document.MaintenanceDocument;

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
