package edu.arizona.kfs.module.cam.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import edu.arizona.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;

public class PurchasingAccountsPayableReportDaoOjb extends org.kuali.kfs.module.cam.dataaccess.impl.PurchasingAccountsPayableReportDaoOjb {
	
	@Override
	protected Collection<String> getDocumentType(Map fieldValues) {
        Collection<String> docTypeCodes = new ArrayList<String>();

        if (fieldValues.containsKey(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE)) {
            String fieldValue = (String) fieldValues.get(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE);
            if (StringUtils.isEmpty(fieldValue)) {
                docTypeCodes.add(CamsConstants.PREQ);
                docTypeCodes.add(CamsConstants.CM);
                docTypeCodes.add(CamsConstants.PRNC); 
            }
            else {
                docTypeCodes.add(fieldValue);
            }
            // truncate the non-property filed
            fieldValues.remove(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE);
        }

        return docTypeCodes;
    }
}