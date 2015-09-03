/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.fp.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CreditCardDetail;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.fp.document.CreditCardReceiptDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.kns.service.BusinessObjectDictionaryService;

/**
 * This class is the struts form for Credit Card Receipt document.
 */
public class CreditCardReceiptForm extends CapitalAccountingLinesFormBase implements CapitalAssetEditable {
    protected CreditCardDetail newCreditCardReceipt;
    protected List<CapitalAssetInformation> capitalAssetInformation;

    /**
     * Constructs a CreditCardReceiptForm.java.
     */
    public CreditCardReceiptForm() {
        super();
        
        setNewCreditCardReceipt(new CreditCardDetail());
        
        capitalAssetInformation = new ArrayList<CapitalAssetInformation>();
        this.capitalAccountingLine.setCanCreateAsset(false); //This document can only edit asset information
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "CCR";
    }
    
    /**
     * @return CreditCardReceiptDocument
     */
    public CreditCardReceiptDocument getCreditCardReceiptDocument() {
        return (CreditCardReceiptDocument) getDocument();
    }

    /**
     * @return CreditCardDetail
     */
    public CreditCardDetail getNewCreditCardReceipt() {
        return newCreditCardReceipt;
    }

    /**
     * @param newCreditCardReceipt
     */
    public void setNewCreditCardReceipt(CreditCardDetail newCreditCardReceipt) {
        this.newCreditCardReceipt = newCreditCardReceipt;
    }

    /**
     * Overrides the parent to call super.populate and then tells each line to check the associated data dictionary and modify the
     * values entered to follow all the attributes set for the values of the accounting line.
     * 
     * @see org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        //
        // now run through all of the accounting lines and make sure they've been uppercased and populated appropriately
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(getNewCreditCardReceipt());

        List<CreditCardDetail> creditCardReceipts = getCreditCardReceiptDocument().getCreditCardReceipts();
        for (CreditCardDetail detail : creditCardReceipts) {
            SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(detail);
        }

    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#getCapitalAssetInformation()
     */
    public List<CapitalAssetInformation> getCapitalAssetInformation() {
        return this.capitalAssetInformation;
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#setCapitalAssetInformation(org.kuali.kfs.fp.businessobject.CapitalAssetInformation)
     */
    public void setCapitalAssetInformation(List<CapitalAssetInformation> capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;        
    }
}
