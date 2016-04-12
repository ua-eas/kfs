/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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

import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * Ad Hoc Route Workgroup Business Object
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@IdClass(AdHocRouteWorkgroupId.class)
@Entity
@Table(name="KRNS_ADHOC_RTE_ACTN_RECIP_T")
public class AdHocRouteWorkgroup extends org.kuali.kfs.krad.bo.AdHocRouteRecipient {
    private static final long serialVersionUID = 1L;

    @Transient
    private String recipientNamespaceCode;

    @Transient
    private String recipientName;

    public AdHocRouteWorkgroup() {
        setType(WORKGROUP_TYPE);
    }

    @Override
    public void setType(Integer type) {
        if (!WORKGROUP_TYPE.equals(type)) {
            throw new IllegalArgumentException("cannot change type to " + type);
        }
        super.setType(type);
    }

    @Override
    public String getName() {
        return "";
    }

    public String getRecipientNamespaceCode() {
        return this.recipientNamespaceCode;
    }

    public String getRecipientName() {
        return this.recipientName;
    }

    public void setRecipientNamespaceCode(String recipientNamespaceCode) {
        this.recipientNamespaceCode = recipientNamespaceCode;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}
