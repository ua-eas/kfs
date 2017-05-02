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
package org.kuali.kfs.coreservice.impl.component;


import org.kuali.kfs.coreservice.api.component.Component;
import org.kuali.kfs.coreservice.api.namespace.NamespaceService;
import org.kuali.kfs.coreservice.framework.component.ComponentEbo;
import org.kuali.kfs.coreservice.impl.namespace.NamespaceBo;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@IdClass(ComponentId.class)
@Entity
@Table(name = "KRCR_CMPNT_T")
public class ComponentBo extends PersistableBusinessObjectBase implements ComponentEbo {

    private static final long serialVersionUID = 1L;

    private static transient NamespaceService namespaceService;

    @Id
    @Column(name = "NMSPC_CD")
    private String namespaceCode;

    @Id
    @Column(name = "CMPNT_CD")
    private String code;

    @Column(name = "NM")
    private String name;

    @Column(name = "ACTV_IND")
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NMSPC_CD", insertable = false, updatable = false)
    private NamespaceBo namespace;

    public String getComponentSetId() {
        return null;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static Component to(ComponentBo bo) {
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
    public static ComponentBo from(Component im) {
        if (im == null) {
            return null;
        }

        ComponentBo bo = new ComponentBo();
        bo.setCode(im.getCode());
        bo.setName(im.getName());
        bo.setActive(im.isActive());
        bo.setNamespaceCode(im.getNamespaceCode());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());

        bo.setNamespace(NamespaceBo.from(namespaceService.getNamespace(bo.namespaceCode)));
        return bo;
    }

    public static void setNamespaceService(NamespaceService namespaceService2) {
        namespaceService = namespaceService2;
    }

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
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

    public NamespaceBo getNamespace() {
        return namespace;
    }

    public void setNamespace(NamespaceBo namespace) {
        this.namespace = namespace;
    }
}

