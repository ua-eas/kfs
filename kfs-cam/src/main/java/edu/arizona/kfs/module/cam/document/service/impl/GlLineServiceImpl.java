package edu.arizona.kfs.module.cam.document.service.impl;

import edu.arizona.kfs.module.cam.document.service.AssetService;

import org.kuali.kfs.module.cam.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;

public class GlLineServiceImpl extends org.kuali.kfs.module.cam.document.service.impl.GlLineServiceImpl {
	
	protected AssetService assetService;
	
	@Override
	protected AssetGlobal createAssetGlobal(GeneralLedgerEntry entry, MaintenanceDocument maintDoc) {
        AssetGlobal assetGlobal = super.createAssetGlobal(entry, maintDoc);
        assetGlobal.setConditionCode(assetService.getNewAssetConditionCode());
        
        return assetGlobal;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }  
}
