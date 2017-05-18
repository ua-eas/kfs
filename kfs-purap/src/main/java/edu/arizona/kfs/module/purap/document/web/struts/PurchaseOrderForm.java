package edu.arizona.kfs.module.purap.document.web.struts;

import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderCapitalAssetLocation;

public class PurchaseOrderForm extends org.kuali.kfs.module.purap.document.web.struts.PurchaseOrderForm {

    @Override
    public CapitalAssetLocation setupNewPurchasingCapitalAssetLocationLine() {
        CapitalAssetLocation location = new PurchaseOrderCapitalAssetLocation();
        return location;
    }

}
