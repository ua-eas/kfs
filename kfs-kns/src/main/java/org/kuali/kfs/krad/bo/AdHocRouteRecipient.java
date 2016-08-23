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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.util.CodeTranslator;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Map;


/**
 * TODO we should not be referencing kew constants from this class and wedding ourselves to that workflow application Ad Hoc Route
 * Recipient Business Object
 *
 *
 */
@MappedSuperclass
public class AdHocRouteRecipient extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = -6499610180752232494L;

    private static Map actionRequestCds;
    public static final Integer PERSON_TYPE = new Integer(0);
    public static final Integer WORKGROUP_TYPE = new Integer(1);

    @Id
	@Column(name="RECIP_TYP_CD")
	protected Integer type;

    @Id
	@Column(name="ACTN_RQST_CD")
	protected String actionRequested;

    @Id
	@Column(name="ACTN_RQST_RECIP_ID")
	protected String id; // can be networkId or group id

    @Transient
    protected String name;

    @Column(name="DOC_HDR_ID")
	protected String documentNumber;

    public AdHocRouteRecipient() {
        // set some defaults that can be overridden
        this.actionRequested = KewApiConstants.ACTION_REQUEST_APPROVE_REQ;
        this.versionNumber = new Long(1);
    }

    public String getActionRequested() {
        return actionRequested;
    }

    public void setActionRequested(String actionRequested) {
        this.actionRequested = actionRequested;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setdocumentNumber (String documentNumber){
        this.documentNumber = documentNumber;
    }

    public String getdocumentNumber (){
        return documentNumber;
    }

    public String getActionRequestedValue() {
        String actionRequestedValue = null;
        if (StringUtils.isNotBlank(getActionRequested())) {
            actionRequestCds.clear();
            actionRequestCds.putAll(CodeTranslator.arLabels);
            actionRequestedValue = (String) actionRequestCds.get(getActionRequested());
        }

        return actionRequestedValue;
    }
}
