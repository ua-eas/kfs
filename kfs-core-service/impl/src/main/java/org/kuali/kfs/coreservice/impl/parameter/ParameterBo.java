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
package org.kuali.kfs.coreservice.impl.parameter;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kuali.kfs.coreservice.api.parameter.EvaluationOperator;
import org.kuali.kfs.coreservice.api.parameter.Parameter;
import org.kuali.kfs.coreservice.impl.parameter.ParameterId;
import org.kuali.kfs.coreservice.impl.parameter.ParameterTypeBo;
import org.kuali.kfs.coreservice.framework.parameter.ParameterEbo;
import org.kuali.kfs.coreservice.impl.component.ComponentBo;
import org.kuali.kfs.coreservice.impl.component.DerivedComponentBo;
import org.kuali.kfs.coreservice.impl.namespace.NamespaceBo;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

@IdClass(ParameterId.class)
@Entity
@Table(name = "KRCR_PARM_T")
public class ParameterBo extends PersistableBusinessObjectBase implements ParameterEbo {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "NMSPC_CD")
    private String namespaceCode;

    @Id
    @Column(name = "CMPNT_CD")
    private String componentCode;

    @Id
    @Column(name = "PARM_NM")
    private String name;

    @Id
    @Column(name = "APPL_ID")
    private String applicationId;

    @Column(name = "VAL")
    private String value;

    @Column(name = "PARM_DESC_TXT", length = 2048)
    private String description;

    @Column(name = "PARM_TYP_CD")
    private String parameterTypeCode;

    @Column(name = "EVAL_OPRTR_CD")
    private String evaluationOperatorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NMSPC_CD", insertable = false, updatable = false)
    private NamespaceBo namespace;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARM_TYP_CD", insertable = false, updatable = false)
    private ParameterTypeBo parameterType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARM_TYP_CD", insertable = false, updatable = false)
    private ComponentBo component;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARM_TYP_CD", insertable = false, updatable = false)
    private DerivedComponentBo derivedComponent;

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static Parameter to(ParameterBo bo) {
        if (bo == null) {
            return null;
        }

        return Parameter.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    public static ParameterBo from(Parameter im) {
        if (im == null) {
            return null;
        }

        ParameterBo bo = new ParameterBo();
        bo.setNamespaceCode(im.getNamespaceCode());
        bo.setComponentCode(im.getComponentCode());
        bo.setName(im.getName());
        bo.setApplicationId(im.getApplicationId());
        bo.setValue(im.getValue());
        bo.setDescription(im.getDescription());
        bo.setParameterTypeCode(im.getParameterType().getCode());
        if(null != im.getEvaluationOperator()){
            bo.setEvaluationOperatorCode(im.getEvaluationOperator().getCode());
        }
        bo.setParameterType(ParameterTypeBo.from(im.getParameterType()));
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }

    @Override
    public ParameterTypeBo getParameterType() {
        return this.parameterType;
    }

    @Override
    public EvaluationOperator getEvaluationOperator() {
        return EvaluationOperator.fromCode(evaluationOperatorCode);
    }

    @Override
    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    @Override
    public String getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParameterTypeCode() {
        return parameterTypeCode;
    }

    public void setParameterTypeCode(String parameterTypeCode) {
        this.parameterTypeCode = parameterTypeCode;
    }

    public void setParameterType(ParameterTypeBo parameterType) {
        this.parameterType = parameterType;
    }

    public String getEvaluationOperatorCode() {
        return evaluationOperatorCode;
    }

    public void setEvaluationOperatorCode(String evaluationOperatorCode) {
        this.evaluationOperatorCode = evaluationOperatorCode;
    }

    public NamespaceBo getNamespace() {
        return namespace;
    }

    public void setNamespace(NamespaceBo namespace) {
        this.namespace = namespace;
    }

    public ComponentBo getComponent() {
        return component;
    }

    public void setComponent(ComponentBo component) {
        this.component = component;
    }

    public DerivedComponentBo getDerivedComponent() {
        return derivedComponent;
    }

    public void setDerivedComponent(DerivedComponentBo derivedComponent) {
        this.derivedComponent = derivedComponent;
    }

}

