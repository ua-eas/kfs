/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.sys.web.struts;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.web.struts.form.KualiForm;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.rice.kim.api.identity.Person;

import java.util.ArrayList;
import java.util.List;

public class ElectronicFundTransferForm extends KualiForm {
    private List<ElectronicPaymentClaim> claims;
    private List<ElectronicPaymentClaimingDocumentGenerationStrategy> availableClaimingDocumentStrategies;
    private List<ElectronicPaymentClaimClaimedHelper> claimedByCheckboxHelpers;
    private String chosenElectronicPaymentClaimingDocumentCode;
    private String hasDocumentation;

    /**
     * Constructs a ElectronicFundTransferForm
     */
    public ElectronicFundTransferForm() {
        claims = new ArrayList<ElectronicPaymentClaim>();
        claimedByCheckboxHelpers = new ArrayList<ElectronicPaymentClaimClaimedHelper>();
    }

    /**
     * Gets the availableClaimingDocumentStrategies attribute.
     *
     * @return Returns the availableClaimingDocumentStrategies.
     */
    public List<ElectronicPaymentClaimingDocumentGenerationStrategy> getAvailableClaimingDocumentStrategies() {
        return availableClaimingDocumentStrategies;
    }

    /**
     * Sets the availableClaimingDocumentStrategies attribute value.
     *
     * @param availableClaimingDocumentStrategies The availableClaimingDocumentStrategies to set.
     */
    public void setAvailableClaimingDocumentStrategies(List<ElectronicPaymentClaimingDocumentGenerationStrategy> availableClaimingDocuments) {
        this.availableClaimingDocumentStrategies = availableClaimingDocuments;
    }

    public boolean hasAvailableClaimingDocumentStrategies() {
        return availableClaimingDocumentStrategies != null && !availableClaimingDocumentStrategies.isEmpty();
    }

    /**
     * Gets the chosenElectronicPaymentClaimingDocumentCode attribute.
     *
     * @return Returns the chosenElectronicPaymentClaimingDocumentCode.
     */
    public String getChosenElectronicPaymentClaimingDocumentCode() {
        return chosenElectronicPaymentClaimingDocumentCode;
    }

    /**
     * Sets the chosenElectronicPaymentClaimingDocumentCode attribute value.
     *
     * @param chosenElectronicPaymentClaimingDocumentCode The chosenElectronicPaymentClaimingDocumentCode to set.
     */
    public void setChosenElectronicPaymentClaimingDocumentCode(String chosenElectronicPaymentClaimingDocumentCode) {
        this.chosenElectronicPaymentClaimingDocumentCode = chosenElectronicPaymentClaimingDocumentCode;
    }

    /**
     * Gets the claims attribute.
     *
     * @return Returns the claims.
     */
    public List<ElectronicPaymentClaim> getClaims() {
        return claims;
    }

    /**
     * Returns the claim at the specified index in the list of claims.
     *
     * @param i index of the claim to return
     * @return the claim at the index
     */
    public ElectronicPaymentClaim getClaim(int i) {
        while (claims.size() <= i) {
            claims.add(new ElectronicPaymentClaim());
        }
        return claims.get(i);
    }

    /**
     * Puts an ElectronicPaymentClaim record in the claims array at a specified point
     *
     * @param claim the claim to add
     * @param i     the index in the list to add the record at
     */
    public void setClaim(ElectronicPaymentClaim claim, int i) {
        while (claims.size() <= i) {
            claims.add(new ElectronicPaymentClaim());
        }
        claims.add(i, claim);
    }

    /**
     * Sets the claims attribute value.
     *
     * @param claims The claims to set.
     */
    public void setClaims(List<ElectronicPaymentClaim> claims) {
        this.claims = claims;
    }

    /**
     * Gets the hasDocumentation attribute.
     *
     * @return Returns the hasDocumentation.
     */
    public String getHasDocumentation() {
        return hasDocumentation;
    }

    /**
     * Sets the hasDocumentation attribute value.
     *
     * @param hasDocumentation The hasDocumentation to set.
     */
    public void setHasDocumentation(String hasDocumentation) {
        this.hasDocumentation = hasDocumentation;
    }

    /**
     * Returns a boolean whether the user has stated that documentation exists for the claims about to be made or not
     *
     * @return true if has documentation, false otherwise
     */
    public boolean isProperlyDocumented() {
        return StringUtils.isNotBlank(this.hasDocumentation) && this.hasDocumentation.equals("Yep");
    }

    /**
     * Returns whether the current user has administrative powers for Electronic Funds Transfer or not
     *
     * @return true if administrative powers exist, false otherwise
     */
    public boolean isAllowElectronicFundsTransferAdministration() {
        Person currentUser = GlobalVariables.getUserSession().getPerson();

        return SpringContext.getBean(ElectronicPaymentClaimingService.class).isAuthorizedForClaimingElectronicPayment(currentUser, null);
    }

    /**
     * @return the key to the EFT documentation message
     */
    public String getDocumentationMessageKey() {
        return KFSKeyConstants.ElectronicPaymentClaim.MESSAGE_EFT_CLAIMING_DOCUMENTATION;
    }

    /**
     * @return the key to the EFT document choice message
     */
    public String getDocumentChoiceMessageKey() {
        return KFSKeyConstants.ElectronicPaymentClaim.MESSAGE_EFT_DOCUMENT_CHOICE;
    }

    /**
     * @return the key to the EFT "previously claimed" message for the table header
     */
    public String getPreviouslyClaimedHeaderKey() {
        return KFSKeyConstants.ElectronicPaymentClaim.MESSAGE_EFT_PREVIOUSLY_CLAIMED_HEADER;
    }

    /**
     * @return the key to the EFT "claiming document number" message for the table header
     */
    public String getClaimingDocumentNumberHeaderKey() {
        return KFSKeyConstants.ElectronicPaymentClaim.MESSAGE_EFT_CLAIMING_DOCUMENT_NUMBER_HEADER;
    }

    /**
     * Gets the claimedByCheckboxHelpers attribute.
     *
     * @return Returns the claimedByCheckboxHelpers.
     */
    public List<ElectronicPaymentClaimClaimedHelper> getClaimedByCheckboxHelpers() {
        return claimedByCheckboxHelpers;
    }

    /**
     * Sets the claimedByCheckboxHelpers attribute value.
     *
     * @param claimedByCheckboxHelpers The claimedByCheckboxHelpers to set.
     */
    public void setClaimedByCheckboxHelpers(List<ElectronicPaymentClaimClaimedHelper> claimedByCheckboxHelpers) {
        this.claimedByCheckboxHelpers = claimedByCheckboxHelpers;
    }

    /**
     * Sets the claimedHelper at the given index
     *
     * @param claimedHelper the claimedCheckboxHelper to set
     * @param index         where in the list it belongs
     */
    public void setClaimedByCheckboxHelper(ElectronicPaymentClaimClaimedHelper claimedHelper, int index) {
        while (claimedByCheckboxHelpers.size() <= index) {
            claimedByCheckboxHelpers.add(new ElectronicPaymentClaimClaimedHelper());
        }
        claimedByCheckboxHelpers.set(index, claimedHelper);
    }

    /**
     * @param index location in the list of ElectronicPaymentClaimClaimedHelpers to return the helper at
     * @return the ElectronicPaymentClaimClaimedHelper at the given location
     */
    public ElectronicPaymentClaimClaimedHelper getClaimedByCheckboxHelper(int index) {
        while (claimedByCheckboxHelpers.size() <= index) {
            claimedByCheckboxHelpers.add(new ElectronicPaymentClaimClaimedHelper());
        }
        return claimedByCheckboxHelpers.get(index);
    }
}

