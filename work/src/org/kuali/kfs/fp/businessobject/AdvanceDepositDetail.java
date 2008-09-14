/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This business object represents the advance deposit detail business object that is used by the Advance Deposit Document.
 */
public class AdvanceDepositDetail extends PersistableBusinessObjectBase {
    private String documentNumber;
    private String financialDocumentTypeCode;
    private String financialDocumentColumnTypeCode;
    private Integer financialDocumentLineNumber;
    private Date financialDocumentAdvanceDepositDate;
    private String financialDocumentAdvanceDepositReferenceNumber;
    private String financialDocumentAdvanceDepositDescription;
    private KualiDecimal financialDocumentAdvanceDepositAmount;
    private String financialDocumentBankCode;

    private AdvanceDepositDocument advanceDepositDocument;
    private Bank bank;

    /**
     * Default constructor.
     */
    public AdvanceDepositDetail() {
        bank = new Bank();
    }
    
    /**
     * Sets the bank code for a new AdvanceDepositDetail to the setup default for the Advance Deposit document.
     */
    public void setDefautBankCode() {
        Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(AdvanceDepositDocument.class);
        if (defaultBank != null) {
            this.financialDocumentBankCode = defaultBank.getBankCode();
            this.bank = defaultBank;
        }
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }


    /**
     * Gets the financialDocumentColumnTypeCode attribute.
     * 
     * @return Returns the financialDocumentColumnTypeCode
     */
    public String getFinancialDocumentColumnTypeCode() {
        return financialDocumentColumnTypeCode;
    }

    /**
     * Sets the financialDocumentColumnTypeCode attribute.
     * 
     * @param financialDocumentColumnTypeCode The financialDocumentColumnTypeCode to set.
     */
    public void setFinancialDocumentColumnTypeCode(String financialDocumentColumnTypeCode) {
        this.financialDocumentColumnTypeCode = financialDocumentColumnTypeCode;
    }


    /**
     * Gets the financialDocumentLineNumber attribute.
     * 
     * @return Returns the financialDocumentLineNumber
     */
    public Integer getFinancialDocumentLineNumber() {
        return financialDocumentLineNumber;
    }

    /**
     * Sets the financialDocumentLineNumber attribute.
     * 
     * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
     */
    public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
        this.financialDocumentLineNumber = financialDocumentLineNumber;
    }


    /**
     * Gets the financialDocumentAdvanceDepositDate attribute.
     * 
     * @return Returns the financialDocumentAdvanceDepositDate
     */
    public Date getFinancialDocumentAdvanceDepositDate() {
        return financialDocumentAdvanceDepositDate;
    }

    /**
     * Sets the financialDocumentAdvanceDepositDate attribute.
     * 
     * @param financialDocumentAdvanceDepositDate The financialDocumentAdvanceDepositDate to set.
     */
    public void setFinancialDocumentAdvanceDepositDate(Date financialDocumentAdvanceDepositDate) {
        this.financialDocumentAdvanceDepositDate = financialDocumentAdvanceDepositDate;
    }

    /**
     * Gets the financialDocumentAdvanceDepositReferenceNumber attribute.
     * 
     * @return Returns the financialDocumentAdvanceDepositReferenceNumber
     */
    public String getFinancialDocumentAdvanceDepositReferenceNumber() {
        return financialDocumentAdvanceDepositReferenceNumber;
    }

    /**
     * Sets the financialDocumentAdvanceDepositReferenceNumber attribute.
     * 
     * @param financialDocumentAdvanceDepositReferenceNumber The financialDocumentAdvanceDepositReferenceNumber to set.
     */
    public void setFinancialDocumentAdvanceDepositReferenceNumber(String financialDocumentAdvanceDepositReferenceNumber) {
        this.financialDocumentAdvanceDepositReferenceNumber = financialDocumentAdvanceDepositReferenceNumber;
    }


    /**
     * Gets the financialDocumentAdvanceDepositDescription attribute.
     * 
     * @return Returns the financialDocumentAdvanceDepositDescription
     */
    public String getFinancialDocumentAdvanceDepositDescription() {
        return financialDocumentAdvanceDepositDescription;
    }

    /**
     * Sets the financialDocumentAdvanceDepositDescription attribute.
     * 
     * @param financialDocumentAdvanceDepositDescription The financialDocumentAdvanceDepositDescription to set.
     */
    public void setFinancialDocumentAdvanceDepositDescription(String financialDocumentAdvanceDepositDescription) {
        this.financialDocumentAdvanceDepositDescription = financialDocumentAdvanceDepositDescription;
    }


    /**
     * Gets the financialDocumentAdvanceDepositAmount attribute.
     * 
     * @return Returns the financialDocumentAdvanceDepositAmount
     */
    public KualiDecimal getFinancialDocumentAdvanceDepositAmount() {
        return financialDocumentAdvanceDepositAmount;
    }

    /**
     * Sets the financialDocumentAdvanceDepositAmount attribute.
     * 
     * @param financialDocumentAdvanceDepositAmount The financialDocumentAdvanceDepositAmount to set.
     */
    public void setFinancialDocumentAdvanceDepositAmount(KualiDecimal financialDocumentAdvanceDepositAmount) {
        this.financialDocumentAdvanceDepositAmount = financialDocumentAdvanceDepositAmount;
    }


    /**
     * Gets the financialDocumentBankCode attribute.
     * 
     * @return Returns the financialDocumentBankCode
     */
    public String getFinancialDocumentBankCode() {
        return financialDocumentBankCode;
    }

    /**
     * Sets the financialDocumentBankCode attribute.
     * 
     * @param financialDocumentBankCode The financialDocumentBankCode to set.
     */
    public void setFinancialDocumentBankCode(String financialDocumentBankCode) {
        this.financialDocumentBankCode = financialDocumentBankCode;
    }

    /**
     * @return AdvanceDepositDocument
     */
    public AdvanceDepositDocument getAdvanceDepositDocument() {
        return advanceDepositDocument;
    }

    /**
     * @param advanceDepositDocument
     */
    public void setAdvanceDepositDocument(AdvanceDepositDocument advanceDepositDocument) {
        this.advanceDepositDocument = advanceDepositDocument;
    }

    /**
     * @return Bank
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * @param bank
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("financialDocumentColumnTypeCode", this.financialDocumentColumnTypeCode);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
}
