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
package org.kuali.kfs.coreservice.impl.parameter;


import org.kuali.kfs.coreservice.api.parameter.ParameterType;
import org.kuali.kfs.coreservice.framework.parameter.ParameterTypeEbo;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "KRCR_PARM_TYP_T")
public class ParameterTypeBo extends PersistableBusinessObjectBase implements ParameterTypeEbo {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PARM_TYP_CD")
    private String code;

    @Column(name = "NM")
    private String name;

    @Column(name = "ACTV_IND")
    private boolean active = true;

    /**
     * Converts a mutable bo to its immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static ParameterType to(ParameterTypeBo bo) {
        if (bo == null) {
            return null;
        }

        return ParameterType.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static ParameterTypeBo from(ParameterType im) {
        if (im == null) {
            return null;
        }

        ParameterTypeBo bo = new ParameterTypeBo();
        bo.setActive(im.isActive());
        bo.setCode(im.getCode());
        bo.setName(im.getName());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

