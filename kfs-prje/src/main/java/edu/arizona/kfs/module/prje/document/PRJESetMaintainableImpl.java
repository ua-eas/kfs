package edu.arizona.kfs.module.prje.document;

import java.util.Map;

import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.kns.document.MaintenanceDocument;

import edu.arizona.kfs.module.prje.businessobject.PRJESet;
import edu.arizona.kfs.module.prje.businessobject.defaultvalue.NextPRJESetIdFinder;

public class PRJESetMaintainableImpl extends FinancialSystemMaintainable {


    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterNew(document, parameters);
        setPRJESetIdIfBlank();
    }
    
    @Override
	public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
		super.processAfterCopy(document, parameters);
		setPRJESetIdIfBlank();
	}

	protected void setPRJESetIdIfBlank() {
        PRJESet prjeSet = (PRJESet) getBusinessObject();
        if (prjeSet.getSetId() == null) {
            prjeSet.setSetId(Integer.valueOf(new NextPRJESetIdFinder().getValue()));
        }
    }
}
