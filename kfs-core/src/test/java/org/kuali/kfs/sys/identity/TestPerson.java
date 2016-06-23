package org.kuali.kfs.sys.identity;

import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;

public class TestPerson implements Person {
    private String principalId = "1234567890";
    private String principalName = "Test";
    
    public TestPerson() {
        
    }
    
    public TestPerson(String principalId, String principalName) {
        this.principalId = principalId;
        this.principalName = principalName;
    }
    
    @Override
    public String getPrincipalId() {
        return principalId;
    }

    @Override
    public String getPrincipalName() {
        return principalName;
    }

    @Override
    public String getEntityId() {
        return null;
    }

    @Override
    public String getEntityTypeCode() {
        return null;
    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public String getFirstNameUnmasked() {
        return null;
    }

    @Override
    public String getMiddleName() {
        return null;
    }

    @Override
    public String getMiddleNameUnmasked() {
        return null;
    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public String getLastNameUnmasked() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getNameUnmasked() {
        return null;
    }

    @Override
    public String getEmailAddress() {
        return null;
    }

    @Override
    public String getEmailAddressUnmasked() {
        return null;
    }

    @Override
    public String getAddressLine1() {
        return null;
    }

    @Override
    public String getAddressLine1Unmasked() {
        return null;
    }

    @Override
    public String getAddressLine2() {
        return null;
    }

    @Override
    public String getAddressLine2Unmasked() {
        return null;
    }

    @Override
    public String getAddressLine3() {
        return null;
    }

    @Override
    public String getAddressLine3Unmasked() {
        return null;
    }

    @Override
    public String getAddressCity() {
        return null;
    }

    @Override
    public String getAddressCityUnmasked() {
        return null;
    }

    @Override
    public String getAddressStateProvinceCode() {
        return null;
    }

    @Override
    public String getAddressStateProvinceCodeUnmasked() {
        return null;
    }

    @Override
    public String getAddressPostalCode() {
        return null;
    }

    @Override
    public String getAddressPostalCodeUnmasked() {
        return null;
    }

    @Override
    public String getAddressCountryCode() {
        return null;
    }

    @Override
    public String getAddressCountryCodeUnmasked() {
        return null;
    }

    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public String getPhoneNumberUnmasked() {
        return null;
    }

    @Override
    public String getCampusCode() {
        return null;
    }

    @Override
    public Map<String, String> getExternalIdentifiers() {
        return null;
    }

    @Override
    public boolean hasAffiliationOfType(String s) {
        return false;
    }

    @Override
    public List<String> getCampusCodesForAffiliationOfType(String s) {
        return null;
    }

    @Override
    public String getEmployeeStatusCode() {
        return null;
    }

    @Override
    public String getEmployeeTypeCode() {
        return null;
    }

    @Override
    public KualiDecimal getBaseSalaryAmount() {
        return null;
    }

    @Override
    public String getExternalId(String s) {
        return null;
    }

    @Override
    public String getPrimaryDepartmentCode() {
        return null;
    }

    @Override
    public String getEmployeeId() {
        return null;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void refresh() {

    }
}

