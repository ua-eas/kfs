/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.sys.identity;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;

import java.util.List;
import java.util.Map;

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

