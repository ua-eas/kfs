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
package org.kuali.kfs.krad.bo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.sql.Timestamp;

/*
 * Defines methods a business object should implement.
 */
public class SessionDocument extends org.kuali.kfs.krad.bo.PersistableBusinessObjectBase {

    private static final long serialVersionUID = 2866566562262830639L;

    @Id
    protected String documentNumber;
    @Id
    protected String sessionId;
    @Column(name = "LAST_UPDT_DT")
    protected Timestamp lastUpdatedDate;
    @Lob
    @Column(name = "SERIALZD_DOC_FRM")
    protected byte[] serializedDocumentForm;
    @Column(name = "CONTENT_ENCRYPTED_IND")
    protected boolean encrypted = false;
    @Id
    protected String principalId;
    @Id
    protected String ipAddress;


    /**
     * @return the serializedDocumentForm
     */
    public byte[] getSerializedDocumentForm() {
        return this.serializedDocumentForm;
    }

    /**
     * @param serializedDocumentForm the serializedDocumentForm to set
     */
    public void setSerializedDocumentForm(byte[] serializedDocumentForm) {
        this.serializedDocumentForm = serializedDocumentForm;
    }


    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    /**
     * @return the lastUpdatedDate
     */
    public Timestamp getLastUpdatedDate() {
        return this.lastUpdatedDate;
    }

    /**
     * @param lastUpdatedDate the lastUpdatedDate to set
     */
    public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    /**
     * @return the documentNumber
     */
    public String getDocumentNumber() {
        return this.documentNumber;
    }

    /**
     * @param documentNumber the documentNumber to set
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * @return the principalId
     */
    public String getPrincipalId() {
        return this.principalId;
    }

    /**
     * @param principalId the principalId to set
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress() {
        return this.ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

}
