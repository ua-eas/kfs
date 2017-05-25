package edu.arizona.kfs.fp.document.service.impl;

import java.util.List;
import java.util.Set;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;

public class DisbursementVoucherPayeeServiceImpl extends org.kuali.kfs.fp.document.service.impl.DisbursementVoucherPayeeServiceImpl {
    
    @Override
    protected void setupFYIs(DisbursementVoucherDocument dvDoc, Set<Person> priorApprovers, String initiatorUserId) {
        List<AdHocRoutePerson> adHocRoutePersons = dvDoc.getAdHocRoutePersons();
        final FinancialSystemTransactionalDocumentAuthorizerBase documentAuthorizer = getDocumentAuthorizer(dvDoc);

        // Add FYI for each approver who has already approved the document
        for (Person approver : priorApprovers) {
            if (documentAuthorizer.canReceiveAdHoc(dvDoc, approver, KewApiConstants.ACTION_REQUEST_FYI_REQ)) {
                String approverPersonUserId = approver.getPrincipalName();
                adHocRoutePersons.add(buildFyiRecipient(approverPersonUserId));
            }
        }

        Person person = KimApiServiceLocator.getPersonService().getPerson(initiatorUserId);
        // Add FYI for initiator
        adHocRoutePersons.add(buildFyiRecipient(person.getPrincipalName()));
    }
}
