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


import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

import java.sql.Date;
import java.sql.Timestamp;


/**
 * Attribute Reference Dummy Business Object
 */
public class AttributeReferenceDummy extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 6582568341825342401L;
    private String oneDigitTextCode;
    private String twoDigitTextCode;
    private String genericSystemId;
    private Date genericDate;
    private Timestamp genericTimestamp;
    private boolean genericBoolean;
    private boolean activeIndicator;
    private KualiDecimal genericAmount;
    private String genericBigText;
    private String emailAddress;
    private KualiPercent percent;
    private boolean newCollectionRecord;
    private String workflowDocumentStatus;
    private Date createDate;
    private String initiatorNetworkId;
    private Date activeFromDate;
    private Date activeToDate;
    private Date activeAsOfDate;
    private boolean current;

    /**
     *
     * Constructs a AttributeReferenceDummy.java.
     *
     */
    public AttributeReferenceDummy() {
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return this.createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the initiatorNetworkId
     */
    public String getInitiatorNetworkId() {
        return this.initiatorNetworkId;
    }

    /**
     * @param initiatorNetworkId the initiatorNetworkId to set
     */
    public void setInitiatorNetworkId(String initiatorNetworkId) {
        this.initiatorNetworkId = initiatorNetworkId;
    }

    /**
     * Gets the percent attribute.
     *
     * @return Returns the percent.
     */
    public KualiPercent getPercent() {
        return percent;
    }

    /**
     * Sets the percent attribute value.
     *
     * @param percent The percent to set.
     */
    public void setPercent(KualiPercent percent) {
        this.percent = percent;
    }


    /**
     * Gets the genericSystemId attribute.
     *
     * @return Returns the genericSystemId.
     */
    public String getGenericSystemId() {
        return genericSystemId;
    }

    /**
     * Sets the genericSystemId attribute value.
     *
     * @param genericSystemId The genericSystemId to set.
     */
    public void setGenericSystemId(String genericSystemId) {
        this.genericSystemId = genericSystemId;
    }

    /**
     * Gets the oneDigitTextCode attribute.
     *
     * @return Returns the oneDigitTextCode.
     */
    public String getOneDigitTextCode() {
        return oneDigitTextCode;
    }

    public Timestamp getGenericTimestamp() {
        return genericTimestamp;
    }

    public void setGenericTimestamp(Timestamp genericTimestamp) {
        this.genericTimestamp = genericTimestamp;
    }

    /**
     * Sets the oneDigitTextCode attribute value.
     *
     * @param oneDigitTextCode The oneDigitTextCode to set.
     */
    public void setOneDigitTextCode(String oneDigitTextCode) {
        this.oneDigitTextCode = oneDigitTextCode;
    }

    /**
     * Gets the twoDigitTextCode attribute.
     *
     * @return Returns the twoDigitTextCode.
     */
    public String getTwoDigitTextCode() {
        return twoDigitTextCode;
    }

    /**
     * Sets the twoDigitTextCode attribute value.
     *
     * @param twoDigitTextCode The twoDigitTextCode to set.
     */
    public void setTwoDigitTextCode(String twoDigitTextCode) {
        this.twoDigitTextCode = twoDigitTextCode;
    }

    /**
     * Gets the genericDate attribute.
     *
     * @return Returns the genericDate.
     */
    public Date getGenericDate() {
        return genericDate;
    }

    /**
     * Sets the genericDate attribute value.
     *
     * @param genericDate The genericDate to set.
     */
    public void setGenericDate(Date genericDate) {
        this.genericDate = genericDate;
    }

    /**
     * Gets the genericBoolean attribute.
     *
     * @return Returns the genericBoolean.
     */
    public boolean isGenericBoolean() {
        return genericBoolean;
    }

    /**
     * Sets the genericBoolean attribute value.
     *
     * @param genericBoolean The genericBoolean to set.
     */
    public void setGenericBoolean(boolean genericBoolean) {
        this.genericBoolean = genericBoolean;
    }

    /**
     * Gets the activeIndicator attribute.
     *
     * @return Returns the activeIndicator.
     */
    public boolean isActiveIndicator() {
        return activeIndicator;
    }

    /**
     * Sets the activeIndicator attribute value.
     *
     * @param activeIndicator The activeIndicator to set.
     */
    public void setActiveIndicator(boolean activeIndicator) {
        this.activeIndicator = activeIndicator;
    }

    /**
     * Gets the genericAmount attribute.
     *
     * @return Returns the genericAmount.
     */
    public KualiDecimal getGenericAmount() {
        return genericAmount;
    }

    /**
     * Sets the genericAmount attribute value.
     *
     * @param genericAmount The genericAmount to set.
     */
    public void setGenericAmount(KualiDecimal genericAmount) {
        this.genericAmount = genericAmount;
    }

    /**
     * Gets the genericBigText attribute.
     *
     * @return Returns the genericBigText.
     */
    public String getGenericBigText() {
        return genericBigText;
    }

    /**
     * Sets the genericBigText attribute value.
     *
     * @param genericBigText The genericBigText to set.
     */
    public void setGenericBigText(String genericBigText) {
        this.genericBigText = genericBigText;
    }

    /**
     * Gets the emailAddress attribute.
     *
     * @return Returns the emailAddress.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the emailAddress attribute value.
     *
     * @param emailAddress The emailAddress to set.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Gets the newCollectionRecord attribute.
     * @return Returns the newCollectionRecord.
     */
    public boolean isNewCollectionRecord() {
        return newCollectionRecord;
    }

    /**
     * Sets the newCollectionRecord attribute value.
     * @param newCollectionRecord The newCollectionRecord to set.
     */
    public void setNewCollectionRecord(boolean newCollectionRecord) {
        this.newCollectionRecord = newCollectionRecord;
    }

    /**
     * @return the workflowDocumentStatus
     */
    public String getWorkflowDocumentStatus() {
        return this.workflowDocumentStatus;
    }

    /**
     * @param workflowDocumentStatus the workflowDocumentStatus to set
     */
    public void setWorkflowDocumentStatus(String workflowDocumentStatus) {
        this.workflowDocumentStatus = workflowDocumentStatus;
    }

    public Date getActiveFromDate() {
		return this.activeFromDate;
	}

	public void setActiveFromDate(Date activeFromDate) {
		this.activeFromDate = activeFromDate;
	}

	public Date getActiveToDate() {
		return this.activeToDate;
	}

	public void setActiveToDate(Date activeToDate) {
		this.activeToDate = activeToDate;
	}

	public Date getActiveAsOfDate() {
		return this.activeAsOfDate;
	}

	public void setActiveAsOfDate(Date activeAsOfDate) {
		this.activeAsOfDate = activeAsOfDate;
	}

	public boolean isCurrent() {
		return this.current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}
}
