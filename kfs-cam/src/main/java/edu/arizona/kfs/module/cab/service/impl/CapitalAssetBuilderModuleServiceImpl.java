package edu.arizona.kfs.module.cab.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.module.cab.CabKeyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class CapitalAssetBuilderModuleServiceImpl extends org.kuali.kfs.module.cab.service.impl.CapitalAssetBuilderModuleServiceImpl {
    private static final Logger LOG = Logger.getLogger(CapitalAssetBuilderModuleService.class);

    @Override
    protected boolean validateUpdateCapitalAssetField(CapitalAssetInformation capitalAssetInformation, AccountingDocument accountingDocument, int index) {
        boolean valid = true;

        Map<String, String> params = new HashMap<String, String>();
        params.put(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, capitalAssetInformation.getCapitalAssetNumber().toString());
        Asset asset = businessObjectService.findByPrimaryKey(Asset.class, params);

        List<Long> assetNumbers = new ArrayList<Long>();
        assetNumbers.add(capitalAssetInformation.getCapitalAssetNumber());

        MessageMap errors = GlobalVariables.getMessageMap();
        errors.removeFromErrorPath(KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "[" + index + "].");
        errors.removeFromErrorPath(capitalAssetInformation.getCapitalAssetActionIndicator().equalsIgnoreCase(KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR) ? KFSPropertyConstants.CAPITAL_ASSET_INFORMATION : KFSPropertyConstants.CAPITAL_ASSET_MODIFY_INFORMATION);
        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        String errorPathPrefix = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION + "[" + index + "]." + KFSPropertyConstants.CAPITAL_ASSET_NUMBER;
        if (ObjectUtils.isNull(asset)) {
            valid = false;
            String label = this.getDataDictionaryService().getAttributeLabel(CapitalAssetInformation.class, KFSPropertyConstants.CAPITAL_ASSET_NUMBER);
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix, KFSKeyConstants.ERROR_EXISTENCE, label);
        } else if (!(this.getAssetService().isCapitalAsset(asset) && !this.getAssetService().isAssetRetired(asset))) {
            // check asset status must be capital asset active.
            valid = false;
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(errorPathPrefix, CabKeyConstants.CapitalAssetInformation.ERROR_ASSET_ACTIVE_CAPITAL_ASSET_REQUIRED);
        } else {
            String documentNumber = accountingDocument.getDocumentNumber();
            String documentType = getDocumentTypeName(accountingDocument);

            if (getCapitalAssetManagementModuleService().isFpDocumentEligibleForAssetLock(accountingDocument, documentType) && getCapitalAssetManagementModuleService().isAssetLocked(assetNumbers, documentType, documentNumber)) {
                valid = false;
            }
        }

        return valid;
    }
}
