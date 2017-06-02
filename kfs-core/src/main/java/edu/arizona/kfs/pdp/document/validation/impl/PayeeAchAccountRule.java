package edu.arizona.kfs.pdp.document.validation.impl;


import java.util.List;

import edu.arizona.kfs.pdp.PdpConstants;
import edu.arizona.kfs.pdp.PdpKeyConstants;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.KimConstants;

public class PayeeAchAccountRule extends org.kuali.kfs.pdp.document.validation.impl.PayeeAchAccountRule {
    
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");

        boolean validEntry = true;

        setupConvenienceObjects();

        validEntry &= validatePayeeId();
        validEntry &= checkForDuplicateRecord();

        return validEntry;
    }
    
    protected boolean validatePayeeId() {
        boolean valid = true;

        String newPayeeIdNumber = newPayeeAchAccount.getPayeeIdNumber();
        String newPayeeIdTypeCd = newPayeeAchAccount.getPayeeIdentifierTypeCode();

        if (PdpConstants.PayeeIdTypeCodes.VENDOR_ID.equals(newPayeeIdTypeCd)) {
            VendorDetail vendorDetail = SpringContext.getBean(VendorService.class).getByVendorNumber(newPayeeIdNumber);
            if (vendorDetail == null) {
                valid = false;
            }
        }
        else if (PdpConstants.PayeeIdTypeCodes.EMPLOYEE.equals(newPayeeIdTypeCd)) {
            Person person = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(newPayeeIdNumber);
            if (person == null) {
                valid = false;
            }
        }
        else if (PdpConstants.PayeeIdTypeCodes.ENTITY.equals(newPayeeIdTypeCd)) {
            Entity entityInfo = SpringContext.getBean(IdentityManagementService.class).getEntityByPrincipalId(newPayeeIdNumber);
            if (entityInfo == null) {
                valid = false;
            }
        }
        else if (PdpConstants.PayeeIdTypeCodes.SSN.equals(newPayeeIdTypeCd)) {
            List<Person> persons = SpringContext.getBean(PersonService.class).getPersonByExternalIdentifier(KimConstants.PersonExternalIdentifierTypes.TAX, newPayeeIdNumber);
            if (persons == null || persons.isEmpty()) {
                valid = false;
            }
        }

        if (!valid) {
            String[] msgParms = new String[] { newPayeeIdNumber, newPayeeIdTypeCd };
            putFieldError(PdpPropertyConstants.PAYEE_ID_NUMBER, PdpKeyConstants.ERROR_PAYEE_ACH_ACCOUNT_INVALID_PAYEE_ID, msgParms);
        }

        return valid;
    }
}
