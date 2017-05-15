package edu.arizona.kfs.module.prje.document;

import java.util.Map;

import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;

import edu.arizona.kfs.module.prje.businessobject.PRJEAccountLine;
import edu.arizona.kfs.module.prje.businessobject.defaultvalue.NextPRJEAccountLineIdFinder;

public class PRJEAccountLineMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);
        setPRJEAccountLineIdIfBlank();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterNew(document, parameters);
        setPRJEAccountLineIdIfBlank();
    }
    
    protected void setPRJEAccountLineIdIfBlank() {
        PRJEAccountLine prjeAccountLine = (PRJEAccountLine) getBusinessObject();
        if (prjeAccountLine.getRateAccountId() == null) {
            prjeAccountLine.setRateAccountId(Integer.valueOf(new NextPRJEAccountLineIdFinder().getValue()));
        }
    }

}
