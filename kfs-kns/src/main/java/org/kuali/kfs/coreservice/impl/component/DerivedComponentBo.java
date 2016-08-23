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
package org.kuali.kfs.coreservice.impl.component;

import org.kuali.kfs.coreservice.api.component.Component;
import org.kuali.kfs.coreservice.api.component.ComponentContract;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@IdClass(ComponentId.class)
@Entity
@Table(name = "KRCR_DRVD_CMPNT_T")
public class DerivedComponentBo extends PersistableBusinessObjectBase implements ComponentContract {

    @Id
    @Column(name = "NMSPC_CD")
    private String namespaceCode;

    @Id
    @Column(name = "CMPNT_CD")
    private String code;

    @Column(name = "NM")
    private String name;

    @Column(name = "CMPNT_SET_ID")
    private String componentSetId;

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public Long getVersionNumber() {
        return null;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static Component to(DerivedComponentBo bo) {
        if (bo == null) {
            return null;
        }
        return Component.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static DerivedComponentBo from(Component im) {
        if (im == null) {
            return null;
        }

        DerivedComponentBo bo = new DerivedComponentBo();
        bo.setCode(im.getCode());
        bo.setName(im.getName());
        bo.setNamespaceCode(im.getNamespaceCode());
        bo.setComponentSetId(im.getComponentSetId());

        return bo;
    }

    public static ComponentBo toComponentBo(DerivedComponentBo derivedComponentBo) {
        if (derivedComponentBo == null) {
            return null;
        }
        Component component = DerivedComponentBo.to(derivedComponentBo);
        return ComponentBo.from(component);
    }

    @Override
    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getComponentSetId() {
        return componentSetId;
    }

    public void setComponentSetId(String componentSetId) {
        this.componentSetId = componentSetId;
    }
}

