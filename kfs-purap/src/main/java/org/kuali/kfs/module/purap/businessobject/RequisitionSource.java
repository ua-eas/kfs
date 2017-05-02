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

package org.kuali.kfs.module.purap.businessobject;

import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

import java.util.LinkedHashMap;

/**
 * Requisition Source Business Object.
 */
public class RequisitionSource extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String requisitionSourceCode;
    private String requisitionSourceDescription;
    private Integer allowCopyDays;
    private boolean active;

    /**
     * Default constructor.
     */
    public RequisitionSource() {

    }

    public String getRequisitionSourceCode() {
        return requisitionSourceCode;
    }

    public void setRequisitionSourceCode(String requisitionSourceCode) {
        this.requisitionSourceCode = requisitionSourceCode;
    }

    public String getRequisitionSourceDescription() {
        return requisitionSourceDescription;
    }

    public void setRequisitionSourceDescription(String requisitionSourceDescription) {
        this.requisitionSourceDescription = requisitionSourceDescription;
    }


    public Integer getAllowCopyDays() {
        return allowCopyDays;
    }

    public void setAllowCopyDays(Integer allowCopyDays) {
        this.allowCopyDays = allowCopyDays;
    }


    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("requisitionSourceCode", this.requisitionSourceCode);
        return m;
    }

}
