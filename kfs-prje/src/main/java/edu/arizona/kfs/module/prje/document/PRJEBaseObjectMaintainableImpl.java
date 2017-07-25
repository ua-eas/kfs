package edu.arizona.kfs.module.prje.document;

import java.util.Map;

import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.kns.document.MaintenanceDocument;

import edu.arizona.kfs.module.prje.businessobject.PRJEBaseObject;
import edu.arizona.kfs.module.prje.businessobject.defaultvalue.NextPRJEBaseObjectIdFinder;

public class PRJEBaseObjectMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);
        setIdIfBlank();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterNew(document, parameters);
        setIdIfBlank();
    }
    
    protected void setIdIfBlank() {
        PRJEBaseObject prjeObj = (PRJEBaseObject) getBusinessObject();
        if (prjeObj.getRateObjectCodeId() == null) {
            prjeObj.setRateObjectCodeId(Integer.valueOf(new NextPRJEBaseObjectIdFinder().getValue()));
        }
    }

}
