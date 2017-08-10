package edu.arizona.kfs.pdp.batch.service.impl;

import java.util.List;

import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.kfs.krad.service.BusinessObjectService;

public class AchAdviceNotificationServiceImpl extends org.kuali.kfs.pdp.batch.service.impl.AchAdviceNotificationServiceImpl {
    private PdpEmailService pdpEmailService;
    private PaymentGroupService paymentGroupService;

    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;

    @Override
    @NonTransactional
    public void sendAdviceNotifications() {
        // get list of payments to send notification for
        List<PaymentGroup> paymentGroups = paymentGroupService.getAchPaymentsNeedingAdviceNotification();
        for (PaymentGroup paymentGroup : paymentGroups) {
            CustomerProfile customer = paymentGroup.getBatch().getCustomerProfile();

            // verify the customer profile is setup to create advices
            if (customer.getAdviceCreate()) {
                pdpEmailService.sendAchAdviceEmail(paymentGroup, null, customer);                
            }

            // update advice sent date on payment
            paymentGroup.setAdviceEmailSentDate(dateTimeService.getCurrentTimestamp());
            businessObjectService.save(paymentGroup);
        }
    }

    @Override
    @NonTransactional
    public void setPdpEmailService(PdpEmailService pdpEmailService) {
        this.pdpEmailService = pdpEmailService;
        super.setPdpEmailService(pdpEmailService);
    }

    @Override
    @NonTransactional
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
        super.setDateTimeService(dateTimeService);
    }

    @Override
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
        super.setBusinessObjectService(businessObjectService);
    }

    @Override
    @NonTransactional
    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
        super.setPaymentGroupService(paymentGroupService);
    }
}
