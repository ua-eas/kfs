package edu.arizona.kfs.pdp.service.impl;

import edu.arizona.kfs.pdp.businessobject.PayeeACHAccount;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.*;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
public class FormatServiceImpl extends org.kuali.kfs.pdp.service.impl.FormatServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatServiceImpl.class);
    
    protected BusinessObjectService businessObjectService;
    protected PaymentGroupService paymentGroupService;
    
    @Override   
    protected boolean processPaymentGroup(PaymentGroup paymentGroup, PaymentProcess paymentProcess) {
        boolean successful = true;

        paymentGroup.setSortValue(paymentGroupService.getSortGroupId(paymentGroup));
        paymentGroup.setPhysCampusProcessCd(paymentProcess.getCampusCode());
        paymentGroup.setProcess(paymentProcess);

        // If any one of the payment details in the group are negative, we always force a check
        boolean noNegativeDetails = true;

        // If any one of the payment details in the group are negative, we always force a check
        List<PaymentDetail> paymentDetailsList = paymentGroup.getPaymentDetails();
        for (PaymentDetail paymentDetail : paymentDetailsList) {
            if (paymentDetail.getNetPaymentAmount().doubleValue() < 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("performFormat() Payment Group " + paymentGroup + " has payment detail net payment amount " + paymentDetail.getNetPaymentAmount());
                    LOG.debug("performFormat() Forcing a Check for Group");
                }
                noNegativeDetails = false;
                break;
            }
        }

        // determine whether payment should be ACH or Check
        CustomerProfile customer = paymentGroup.getBatch().getCustomerProfile();

        PayeeACHAccount payeeAchAccount = null;
        boolean isCheck = true;
        if (PdpConstants.PayeeIdTypeCodes.VENDOR_ID.equals(paymentGroup.getPayeeIdTypeCd()) || PdpConstants.PayeeIdTypeCodes.EMPLOYEE.equals(paymentGroup.getPayeeIdTypeCd()) || PdpConstants.PayeeIdTypeCodes.ENTITY.equals(paymentGroup.getPayeeIdTypeCd())) {
            if (StringUtils.isNotBlank(paymentGroup.getPayeeId()) && !paymentGroup.getPymtAttachment() && !paymentGroup.getProcessImmediate() && !paymentGroup.getPymtSpecialHandling() && (customer.getAchTransactionType() != null) && noNegativeDetails) {
                LOG.debug("performFormat() Checking ACH");
                payeeAchAccount = getAchInformation(paymentGroup.getPayeeIdTypeCd(), paymentGroup.getPayeeId(), customer.getAchTransactionType());
                isCheck = (payeeAchAccount == null);
            }
        }

        DisbursementType disbursementType = null;
        if (isCheck) {
            PaymentStatus paymentStatus = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.PENDING_CHECK);
            paymentGroup.setPaymentStatus(paymentStatus);

            disbursementType = businessObjectService.findBySinglePrimaryKey(DisbursementType.class, PdpConstants.DisbursementTypeCodes.CHECK);
            paymentGroup.setDisbursementType(disbursementType);
        }
        else {
            PaymentStatus paymentStatus = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.PENDING_ACH);
            paymentGroup.setPaymentStatus(paymentStatus);

            disbursementType = businessObjectService.findBySinglePrimaryKey(DisbursementType.class, PdpConstants.DisbursementTypeCodes.ACH);
            paymentGroup.setDisbursementType(disbursementType);

            paymentGroup.setAchBankRoutingNbr(payeeAchAccount.getBankRoutingNumber());
            paymentGroup.setAdviceEmailAddress(payeeAchAccount.getPayeeEmailAddress());
            paymentGroup.setAchAccountType(payeeAchAccount.getBankAccountTypeCode());

            AchAccountNumber achAccountNumber = new AchAccountNumber();
            achAccountNumber.setAchBankAccountNbr(payeeAchAccount.getBankAccountNumber());
            achAccountNumber.setId(paymentGroup.getId());
            paymentGroup.setAchAccountNumber(achAccountNumber);
        }

        // set payment group bank
        successful &= super.validateAndUpdatePaymentGroupBankCode(paymentGroup, disbursementType, customer);

        return successful;
    }
    
    protected PayeeACHAccount getAchInformation(String idType, String payeeId, String achTransactionType) {
        LOG.debug("getAchInformation() started");

        Map<String, Object> fields = new HashMap<String, Object>();

        fields.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        fields.put(PdpPropertyConstants.PAYEE_IDENTIFIER_TYPE_CODE, idType);
        fields.put(PdpPropertyConstants.ACH_TRANSACTION_TYPE, achTransactionType);
        fields.put(PdpPropertyConstants.PAYEE_ID_NUMBER, payeeId);

        Collection<PayeeACHAccount> rows = businessObjectService.findMatching(PayeeACHAccount.class, fields);
        if (rows.size() != 1) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getAchInformation() not found rows = " + rows.size());
            }

            return null;
        }
        else {
            LOG.debug("getAchInformation() found");

            return rows.iterator().next();
        }
    }
        
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
        super.setBusinessObjectService(businessObjectService);
    }
   
    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
        super.setPaymentGroupService(paymentGroupService);
    }
     
}
