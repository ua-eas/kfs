package edu.arizona.kfs.pdp.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.pdp.businessobject.AccountingChangeCode;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PayeeType;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentAccountHistory;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.kfs.krad.bo.KualiCodeBase;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.krad.util.ObjectUtils;

import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.gl.service.GlobalTransactionEditService;
import edu.arizona.kfs.pdp.PdpKeyConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;

/**
 * @see org.kuali.kfs.pdp.batch.service.PaymentFileValidationService
 */
@Transactional
public class PaymentFileValidationServiceImpl extends org.kuali.kfs.pdp.service.impl.PaymentFileValidationServiceImpl {
	
	private static final Pattern DUPLICATE_WHITESPACE = Pattern.compile("\\s{2,}");
	
	protected GlobalTransactionEditService globalTransactionEditService;
    protected VendorService vendorService;
        
    /**
     * Override base to remove error check for negative group payment totals. 
     * This will now become a warning and the payments will be placed in held status
     * 
     * Validates payment file groups <li>Checks number of note lines needed is not above the configured maximum allowed</li> <li>
     * Verifies group total is not negative</li> <li>Verifies detail accounting total equals net payment amount</li>
     *
     * @param paymentFile payment file object
     * @param errorMap map in which errors will be added to
     */
    @Override
    protected void processGroupValidation(PaymentFileLoad paymentFile, MessageMap errorMap) {
        int groupCount = 0;
        for (PaymentGroup paymentGroup : paymentFile.getPaymentGroups()) {
            groupCount++;
            int noteLineCount = 0;
            int detailCount = 0;

            // We've encountered Payment Files that have address lines exceeding the column size in DB table;
            // so adding extra validation on payment group BO, especially the max length, based on DD definitions.
            // Check that PaymentGroup String properties don't exceed maximum allowed length
            super.checkPaymentGroupPropertyMaxLength(paymentGroup, errorMap);

            // verify payee id and owner code if customer requires them to be filled in
            if (paymentFile.getCustomer().getPayeeIdRequired() && StringUtils.isBlank(paymentGroup.getPayeeId())) {
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_PAYEE_ID_REQUIRED, Integer.toString(groupCount));
            }

            if (paymentFile.getCustomer().getOwnershipCodeRequired() && StringUtils.isBlank(paymentGroup.getPayeeOwnerCd())) {
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_PAYEE_OWNER_CODE, Integer.toString(groupCount));
            }

            // validate payee id type
            if (StringUtils.isNotBlank(paymentGroup.getPayeeIdTypeCd())) {
                PayeeType payeeType = businessObjectService.findBySinglePrimaryKey(PayeeType.class, paymentGroup.getPayeeIdTypeCd());
                if (payeeType == null) {
                    errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_INVALID_PAYEE_ID_TYPE, Integer.toString(groupCount), paymentGroup.getPayeeIdTypeCd());
                }
            }

            // validate bank
            String bankCode = paymentGroup.getBankCode();
            if (StringUtils.isNotBlank(bankCode)) {
                Bank bank = bankService.getByPrimaryId(bankCode);
                if (bank == null) {
                    errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_INVALID_BANK_CODE, Integer.toString(groupCount), bankCode);
                }
                else if (!bank.isActive()) {
                    errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_INACTIVE_BANK_CODE, Integer.toString(groupCount), bankCode);
                }
            }

            for (PaymentDetail paymentDetail : paymentGroup.getPaymentDetails()) {
                detailCount++;

                noteLineCount++; // Add a line to print the invoice number
                noteLineCount = noteLineCount + paymentDetail.getNotes().size();

                if ((paymentDetail.getNetPaymentAmount() == null) && (!paymentDetail.isDetailAmountProvided())) {
                    paymentDetail.setNetPaymentAmount(paymentDetail.getAccountTotal());
                }
                else if ((paymentDetail.getNetPaymentAmount() == null) && (paymentDetail.isDetailAmountProvided())) {
                    paymentDetail.setNetPaymentAmount(paymentDetail.getCalculatedPaymentAmount());
                }

                // compare net to accounting segments
                if (paymentDetail.getAccountTotal().compareTo(paymentDetail.getNetPaymentAmount()) != 0) {
                    errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_DETAIL_TOTAL_MISMATCH, Integer.toString(groupCount), Integer.toString(detailCount), paymentDetail.getAccountTotal().toString(), paymentDetail.getNetPaymentAmount().toString());
                }

                // validate origin code if given
                if (StringUtils.isNotBlank(paymentDetail.getFinancialSystemOriginCode())) {
                    OriginationCode originationCode = originationCodeService.getByPrimaryKey(paymentDetail.getFinancialSystemOriginCode());
                    if (originationCode == null) {
                        errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_INVALID_ORIGIN_CODE, Integer.toString(groupCount), Integer.toString(detailCount), paymentDetail.getFinancialSystemOriginCode());
                    }
                }

                // validate doc type if given
                if (StringUtils.isNotBlank(paymentDetail.getFinancialDocumentTypeCode())) {
                    if ( !documentTypeService.isActiveByName(paymentDetail.getFinancialDocumentTypeCode()) ) {
                        errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_INVALID_DOC_TYPE, Integer.toString(groupCount), Integer.toString(detailCount), paymentDetail.getFinancialDocumentTypeCode());
                    }
                }
                
            }

            // verify total for group is not negative
            // moved negative group total validation to processGroupSoftEdits()
           
            // check that the number of detail items and note lines will fit on a check stub
            if (noteLineCount > getMaxNoteLines()) {
                errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_PAYMENT_LOAD_MAX_NOTE_LINES, Integer.toString(groupCount), Integer.toString(noteLineCount), Integer.toString(getMaxNoteLines()));
            }
        }
    }

    /**
     * Adds additional validation to the payment group.  If the payment fails any of the validation rules, it is removed from the batch.
     * 
     * Rules:
     *      Payee ID must be a valid vendor ID
     *      Vendor Record must be active
     *      If the customer requires an ownership code, the ownership code on the record must match that on the vendor table
     *      
     * @param paymentFile payment file object
     * @param customer payment customer
     * @param warnings <code>List</code> list of accumulated warning messages
     */
    @Override
    public void processGroupSoftEdits(PaymentFileLoad paymentFile, CustomerProfile customer, List<String> warnings) {
    	//do UA validations first - we need to remove any failures before the baseline checks
    	List<PaymentGroup> paymentGroupsToHold = new ArrayList<PaymentGroup>();
    	
    	if (customer.getPayeeIdRequired() || customer.getAccountingEditRequired()) {
    		
    		int groupCount = 0;            
            Iterator<PaymentGroup> paymentGroups = paymentFile.getPaymentGroups().iterator();
            while (paymentGroups.hasNext()) {
                PaymentGroup pg = paymentGroups.next();
                groupCount++;
                
                // only do accounting edits if required by customer
                if (customer.getAccountingEditRequired()) {
                    if (processAccountingEdits(paymentFile, pg, groupCount, warnings)) {
                        // remove the line from the batch
                        paymentGroups.remove();
                        removeGroupFromFileTotals(paymentFile, pg);                        
                        continue; //group removed, so process next group 
                    }
                }
                
                // none of these checks need to be made if the customer profile does not require a payee ID
                if (customer.getPayeeIdRequired()) {                	
                	// verify that payee exists and is active if payee required by customer
                    if (StringUtils.isNotBlank(pg.getPayeeId()) && PdpConstants.PayeeIdTypeCodes.VENDOR_ID.equals(pg.getPayeeIdTypeCd())) {
                        
                    	// look up the vendor detail record
                        VendorDetail v = vendorService.getVendorDetail(pg.getPayeeId());
                        if (v == null) {
                        	super.addWarningMessage(warnings, PdpKeyConstants.ERROR_VENDOR_DOES_NOT_EXIST, Integer.toString(groupCount), pg.getPayeeId(), pg.getPayeeName(), pg.getNetPaymentAmount().toString());
                            // remove the line from the batch
                            paymentGroups.remove();
                            removeGroupFromFileTotals(paymentFile, pg);
                            continue;
                        } else { 
                            // check the active flag
                            if (!v.isActiveIndicator()) {
                            	super.addWarningMessage(warnings, PdpKeyConstants.ERROR_VENDOR_INACTIVE, Integer.toString(groupCount), pg.getPayeeId(), pg.getPayeeName(), pg.getNetPaymentAmount().toString() );
                                // add group to list to be HELD
                                paymentGroupsToHold.add(pg);
                            }
                        }
                        
                        // check the payee name against the vendor name on file
                        if (!validateVendorName(pg, v, warnings, groupCount)) {
                            // add group to list to be HELD
                            paymentGroupsToHold.add(pg);
                        }
                        
                        // check for negative payment total
                        if (pg.getNetPaymentAmount().doubleValue()  < 0) {
                        	super.addWarningMessage(warnings, PdpKeyConstants.ERROR_NEGATIVE_PAYMENT_TOTAL, Integer.toString(groupCount), pg.getPayeeId(), pg.getPayeeName(), pg.getNetPaymentAmount().toString() );
                        	// add group to list to be HELD
                        	paymentGroupsToHold.add(pg);
                        }
                        
                        // check for future dated invoice
                        if (hasFutureDatedInvoice(pg)) {
                        	super.addWarningMessage(warnings, PdpKeyConstants.ERROR_FUTURE_DATED_INVOICE, Integer.toString(groupCount), pg.getPayeeId(), pg.getPayeeName(), pg.getNetPaymentAmount().toString() );
                        	// add group to list to be HELD
                        	paymentGroupsToHold.add(pg);                            
                        }
                                               
                        // validate the ownership code
                        if (customer.getOwnershipCodeRequired()) {
                            // verify that the ownership code matches the vendor
                            if (!StringUtils.equals(pg.getPayeeOwnerCd(), v.getVendorHeader().getVendorOwnershipCode())) {
                            	super.addWarningMessage(warnings, PdpKeyConstants.ERROR_VENDOR_OWNERSHIP_MISMATCH, Integer.toString(groupCount), pg.getPayeeOwnerCd(), v.getVendorHeader().getVendorOwnershipCode(), pg.getPayeeName(), pg.getNetPaymentAmount().toString() );
                            	// add group to list to be HELD
                                paymentGroupsToHold.add(pg);
                            }
                        }
    
                        // get the appropriate address
                        VendorAddress va  = determineAddress(v, customer);
                        if ( va != null ) {
                            // update the address information
                            pg.setLine1Address(va.getVendorLine1Address());
                            pg.setLine2Address(va.getVendorLine2Address());
                            pg.setLine3Address(KFSConstants.EMPTY_STRING);
                            pg.setLine4Address(KFSConstants.EMPTY_STRING);
                            pg.setCity(va.getVendorCityName());
                            pg.setState(va.getVendorStateCode());
                            pg.setZipCd(va.getVendorZipCode());
                            pg.setCountry(va.getVendorCountryCode());
                            pg.setCampusAddress(Boolean.FALSE);
                        }
                        
                    }
                }
            }
        }       

    	super.processGroupSoftEdits(paymentFile, customer, warnings);
    	
    	// set payment to HELD, regardless of previous edits 
        if (!paymentGroupsToHold.isEmpty()) {
        	PaymentStatus heldStatus = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.HELD_CD);
            for (PaymentGroup pg : paymentGroupsToHold) {
                pg.setPaymentStatusCode(PdpConstants.PaymentStatusCodes.HELD_CD);
                pg.setPaymentStatus(heldStatus);
            }
        }
       
    }

    /*
     * Needed to override in order to call our version of processAccountSoftEdits(), which has
     * a different method signature than Foundation's provided.
     */
    @Override
    protected void processDetailSoftEdits(PaymentFileLoad paymentFile, CustomerProfile customer, PaymentDetail paymentDetail, List<String> warnings) {
        updateDetailAmounts(paymentDetail);

        // Check net payment amount
        KualiDecimal testAmount = paymentDetail.getNetPaymentAmount();
        if (testAmount.compareTo(customer.getPaymentThresholdAmount()) > 0) {
            addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_DETAIL_THRESHOLD, testAmount.toString(), customer.getPaymentThresholdAmount().toString());
            paymentFile.setDetailThreshold(true);
            paymentFile.getThresholdPaymentDetails().add(paymentDetail);
        }

        // set invoice date if it doesn't exist
        if (paymentDetail.getInvoiceDate() == null) {
            paymentDetail.setInvoiceDate(dateTimeService.getCurrentSqlDate());
        }

        if (paymentDetail.getPrimaryCancelledPayment() == null) {
            paymentDetail.setPrimaryCancelledPayment(Boolean.FALSE);
        }

        // do accounting edits
        for (PaymentAccountDetail paymentAccountDetail : paymentDetail.getAccountDetail()) {
            paymentAccountDetail.setPaymentDetailId(paymentDetail.getId());

            processAccountSoftEdits(customer, paymentAccountDetail, warnings, paymentDetail);
        }
    }



    /**
     * Set default fields on account line and perform account field existence checks
     *
     * KATTS Note: I changed the method scope here to expelempify that this extension necessiates
     *             a departure from the foundation signature. This was due to how the legacy GTE
     *             code was implemented, and I wanted to ensure logic changes only occured in the
     *             edu.arizona packgage. The compramise bing that the foundation class's scope was
     *             changed too, but its method args remained the same.
     *
     * @param paymentFile payment file object
     * @param customer payment customer
     * @param paymentAccountDetail <code>PaymentAccountDetail</code> object to process
     * @param warnings <code>List</code> list of accumulated warning messages
     * @param paymentDetail needed for several codes for a call to the GTE service
     */
    private void processAccountSoftEdits(CustomerProfile customer, PaymentAccountDetail paymentAccountDetail, List<String> warnings, PaymentDetail paymentDetail) {
        List<PaymentAccountHistory> changeRecords = paymentAccountDetail.getAccountHistory();

        // uppercase chart
        paymentAccountDetail.setFinChartCode(paymentAccountDetail.getFinChartCode().toUpperCase());

        // only do accounting edits if required by customer
        if (customer.getAccountingEditRequired()) {
            // check account number
            Account account = accountService.getByPrimaryId(paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr());
            if (account == null) {
                addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_INVALID_ACCOUNT, paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr());

                KualiCodeBase objChangeCd = businessObjectService.findBySinglePrimaryKey(AccountingChangeCode.class, PdpConstants.AccountChangeCodes.INVALID_ACCOUNT);
                replaceAccountingString(objChangeCd, changeRecords, customer, paymentAccountDetail);
            }
            else {

                account.refreshReferenceObject("subFundGroup");
                paymentAccountDetail.refreshReferenceObject("objectCode");
                Message result = globalTransactionEditService.isAccountingLineAllowable(
                        paymentDetail.getFinancialSystemOriginCode(),
                        account.getSubFundGroup().getFundGroupCode(),
                        account.getSubFundGroupCode(),
                        paymentDetail.getFinancialDocumentTypeCode(),
                        paymentAccountDetail.getObjectCode().getFinancialObjectTypeCode(),
                        paymentAccountDetail.getObjectCode().getFinancialObjectSubTypeCode());
                if (result != null) {
                    warnings.add(result.getMessage());
                    addWarningMessage(warnings, KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_ACCOUNT_NUMBER_CHANGED, paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr());
                    KualiCodeBase objChangeCd = businessObjectService.findBySinglePrimaryKey(AccountingChangeCode.class, PdpConstants.AccountChangeCodes.INVALID_ACCOUNT);
                    replaceAccountingString(objChangeCd, changeRecords, customer, paymentAccountDetail);
                }

                // check sub account code
                if (StringUtils.isNotBlank(paymentAccountDetail.getSubAccountNbr())) {
                    SubAccount subAccount = subAccountService.getByPrimaryId(paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr(), paymentAccountDetail.getSubAccountNbr());
                    if (subAccount == null) {
                        addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_INVALID_SUB_ACCOUNT, paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr(), paymentAccountDetail.getSubAccountNbr());

                        KualiCodeBase objChangeCd = businessObjectService.findBySinglePrimaryKey(AccountingChangeCode.class, PdpConstants.AccountChangeCodes.INVALID_SUB_ACCOUNT);
                        changeRecords.add(newAccountHistory(PdpPropertyConstants.SUB_ACCOUNT_DB_COLUMN_NAME, KFSConstants.getDashSubAccountNumber(), paymentAccountDetail.getSubAccountNbr(), objChangeCd));

                        paymentAccountDetail.setSubAccountNbr(KFSConstants.getDashSubAccountNumber());
                    }
                }

                // check object code
                ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getFinObjectCode());
                if (objectCode == null) {
                    addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_INVALID_OBJECT, paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getFinObjectCode());

                    KualiCodeBase objChangeCd = businessObjectService.findBySinglePrimaryKey(AccountingChangeCode.class, PdpConstants.AccountChangeCodes.INVALID_OBJECT);
                    replaceAccountingString(objChangeCd, changeRecords, customer, paymentAccountDetail);
                }

                // check sub object code
                else if (StringUtils.isNotBlank(paymentAccountDetail.getFinSubObjectCode())) {
                    SubObjectCode subObjectCode = subObjectCodeService.getByPrimaryIdForCurrentYear(paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr(), paymentAccountDetail.getFinObjectCode(), paymentAccountDetail.getFinSubObjectCode());
                    if (subObjectCode == null) {
                        addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_INVALID_SUB_OBJECT, paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr(), paymentAccountDetail.getFinObjectCode(), paymentAccountDetail.getFinSubObjectCode());

                        KualiCodeBase objChangeCd = businessObjectService.findBySinglePrimaryKey(AccountingChangeCode.class, PdpConstants.AccountChangeCodes.INVALID_SUB_OBJECT);
                        changeRecords.add(newAccountHistory(PdpPropertyConstants.SUB_OBJECT_DB_COLUMN_NAME, KFSConstants.getDashFinancialSubObjectCode(), paymentAccountDetail.getFinSubObjectCode(), objChangeCd));

                        paymentAccountDetail.setFinSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                    }
                }
            }

            // check project code
            if (StringUtils.isNotBlank(paymentAccountDetail.getProjectCode())) {
                ProjectCode projectCode = businessObjectService.findBySinglePrimaryKey(ProjectCode.class, paymentAccountDetail.getProjectCode());
                if (projectCode == null) {
                    addWarningMessage(warnings, PdpKeyConstants.MESSAGE_PAYMENT_LOAD_INVALID_PROJECT, paymentAccountDetail.getProjectCode());

                    KualiCodeBase objChangeCd = businessObjectService.findBySinglePrimaryKey(AccountingChangeCode.class, PdpConstants.AccountChangeCodes.INVALID_PROJECT);
                    changeRecords.add(newAccountHistory(PdpPropertyConstants.PROJECT_DB_COLUMN_NAME, KFSConstants.getDashProjectCode(), paymentAccountDetail.getProjectCode(), objChangeCd));
                    paymentAccountDetail.setProjectCode(KFSConstants.getDashProjectCode());
                }
            }
        }

        // change nulls into ---'s for the fields that need it
        if (StringUtils.isBlank(paymentAccountDetail.getFinSubObjectCode())) {
            paymentAccountDetail.setFinSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }

        if (StringUtils.isBlank(paymentAccountDetail.getSubAccountNbr())) {
            paymentAccountDetail.setSubAccountNbr(KFSConstants.getDashSubAccountNumber());
        }

        if (StringUtils.isBlank(paymentAccountDetail.getProjectCode())) {
            paymentAccountDetail.setProjectCode(KFSConstants.getDashProjectCode());
        }

    }
    
    protected void removeGroupFromFileTotals(PaymentFileLoad paymentFile, PaymentGroup pg) {
        for (PaymentDetail pd : pg.getPaymentDetails()) {
            removeDetailFromFileTotals(paymentFile, pd); 
        }
    }
    
    protected void removeDetailFromFileTotals(PaymentFileLoad paymentFile, PaymentDetail pd) {
        paymentFile.setPaymentCount(paymentFile.getPaymentCount() - 1);
        paymentFile.setPaymentTotalAmount(paymentFile.getPaymentTotalAmount().subtract(pd.getNetPaymentAmount()));
    }
    
    protected boolean processAccountingEdits(PaymentFileLoad paymentFile, PaymentGroup paymentGroup, int groupCount, List<String> warnings) {
        boolean removeGroup = false;
        
        for (PaymentDetail paymentDetail : paymentGroup.getPaymentDetails()) {   
            
            for (PaymentAccountDetail paymentAccountDetail : paymentDetail.getAccountDetail()) {                                
                // get account info
                Account account = accountService.getByPrimaryId(paymentAccountDetail.getFinChartCode(), paymentAccountDetail.getAccountNbr());
                if (account != null) {                     
                    paymentAccountDetail.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);                   
                    if (ObjectUtils.isNull(paymentAccountDetail.getObjectCode())) {
                        super.addWarningMessage(warnings, PdpKeyConstants.ERROR_OBJECT_CODE_DOES_NOT_EXIST, Integer.toString(groupCount), account.getAccountNumber(), paymentGroup.getPayeeName(), paymentGroup.getNetPaymentAmount().toString());
                        removeGroup = true;                    
                    }
                }
            }
            
        } 
        return removeGroup;  
    }
    
    protected boolean validateVendorName(PaymentGroup pg, VendorDetail v, List<String> warnings, int groupCount) {
        String vendorName = v.getVendorName();
        String payeeName = pg.getPayeeName();
        payeeName = payeeName.toUpperCase().trim();
        payeeName = DUPLICATE_WHITESPACE.matcher(payeeName).replaceAll(" ");
        
        if (StringUtils.isNotBlank(vendorName)) {
            vendorName = vendorName.toUpperCase().trim();
            vendorName = DUPLICATE_WHITESPACE.matcher(vendorName).replaceAll(" ");
            if (!StringUtils.equals(vendorName, payeeName)) {
            	super.addWarningMessage(warnings, PdpKeyConstants.ERROR_VENDOR_NAME_MISMATCH, Integer.toString(groupCount), pg.getPayeeId(), pg.getPayeeName(), v.getVendorName(), pg.getNetPaymentAmount().toString());
                return false;
            }
        } // no else - if the vendor name is null, we're just going to punt here and assume there is a data problem with KFS
        
        return true;
    }
    
    /**
     * searches through payment group and returns true if any detail has a future dated invoice
     * @param paymentGroup
     * @return
     */
    protected boolean hasFutureDatedInvoice(PaymentGroup paymentGroup) {
        boolean retval = false;
    
        // if we have details sum up the payment amount for the group
        if (paymentGroup.getPaymentDetails() != null) {
            Date curdate = DateUtils.round(new Date(), Calendar.DAY_OF_MONTH);
            
            for (PaymentDetail paymentDetail : paymentGroup.getPaymentDetails()) {
                if (paymentDetail.getInvoiceDate() != null) {
                    Date invdate = DateUtils.round(paymentDetail.getInvoiceDate(), Calendar.DAY_OF_MONTH);
                    if (invdate.getTime() > curdate.getTime()) {
                        retval = true;
                        break;
                    }
                }
            }
        }
        
        return retval;
    }
    
    protected VendorAddress determineAddress(VendorDetail v, CustomerProfile customer) {
    	VendorAddress va = null;
    	
    	// try to get a "remit" address first
        va = vendorService.getVendorDefaultAddress(
        		v.getVendorHeaderGeneratedIdentifier(), 
        		v.getVendorDetailAssignedIdentifier(), 
        		VendorConstants.AddressTypes.REMIT, customer.getDefaultPhysicalCampusProcessingCode());
        
        // if that fails, go for a PO address
        if ( va == null ) {
            va = vendorService.getVendorDefaultAddress(
                    v.getVendorHeaderGeneratedIdentifier(), 
                    v.getVendorDetailAssignedIdentifier(), 
                    VendorConstants.AddressTypes.PURCHASE_ORDER, customer.getDefaultPhysicalCampusProcessingCode() );
        }
        
        return va;
    }
    
    public void setGlobalTransactionEditService(GlobalTransactionEditService globalTransactionEditService) {
        this.globalTransactionEditService = globalTransactionEditService;
    }
    
    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

}
