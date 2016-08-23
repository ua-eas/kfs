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
package org.kuali.kfs.kim.impl.identity

import org.kuali.rice.kim.api.identity.Person
import org.kuali.rice.core.api.util.type.KualiDecimal

/**
 * Stub Person impl
 */
class TestPerson implements Person {
    String getPrincipalId() { return null }
    String getPrincipalName() { return null }
    String getEntityId() { return null }
    String getEntityTypeCode() { return null }
    String getFirstName() { return null }
    String getFirstNameUnmasked() { return null }
    String getMiddleName() { return null }
    String getMiddleNameUnmasked() { return null }
    String getLastName() { return null }
    String getLastNameUnmasked() { return null }
    String getName() { return null }
    String getNameUnmasked() { return null }
    String getEmailAddress() { return null }
    String getEmailAddressUnmasked() { return null }
    String getAddressLine1() { return null }
    String getAddressLine1Unmasked() { return null }
    String getAddressLine2() { return null }
    String getAddressLine2Unmasked() { return null }
    String getAddressLine3() { return null }
    String getAddressLine3Unmasked() { return null }
    String getAddressCity() { return null }
    String getAddressCityUnmasked() { return null }
    String getAddressStateProvinceCode() { return null }
    String getAddressStateProvinceCodeUnmasked() { return null }
    String getAddressPostalCode() { return null }
    String getAddressPostalCodeUnmasked() { return null }
    String getAddressCountryCode() { return null }
    String getAddressCountryCodeUnmasked() { return null }
    String getPhoneNumber() { return null }
    String getPhoneNumberUnmasked() { return null }
    String getCampusCode() { return null }
    Map<String, String> getExternalIdentifiers() { return null }
    boolean hasAffiliationOfType(String affiliationTypeCode) { return false }
    List<String> getCampusCodesForAffiliationOfType(String affiliationTypeCode) { return null }
    String getEmployeeStatusCode() { return null }
    String getEmployeeTypeCode() { return null }
    KualiDecimal getBaseSalaryAmount() { return null }
    String getExternalId(String externalIdentifierTypeCode) { return null }
    String getPrimaryDepartmentCode() { return null }
    String getEmployeeId() { return null }
    boolean isActive() { return false }
    void refresh() { }
}
