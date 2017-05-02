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
package org.kuali.kfs.krad.datadictionary.validation.result;

import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.processor.ConstraintProcessor;

import java.util.LinkedList;
import java.util.List;

/**
 * This is a composite class for all the different members that need to be returned when a {@link ConstraintProcessor}
 * processes a {@link Constraint}.
 */
public class ConstraintValidationResult {

    private String entryName;
    private String attributeName;
    private String attributePath;
    private String constraintName;
    private ErrorLevel level;

    private String errorKey;
    private String[] errorParameters = {};
    private String constraintLabelKey;

    private List<ConstraintValidationResult> children;


    public ConstraintValidationResult(String constraintName) {
        this.constraintName = constraintName;
        this.children = new LinkedList<ConstraintValidationResult>();
        this.level = ErrorLevel.OK;
    }

    public ConstraintValidationResult(String constraintName, ErrorLevel level) {
        this.constraintName = constraintName;
        this.children = new LinkedList<ConstraintValidationResult>();
        this.level = level;
    }

    public void addChild(ConstraintValidationResult child) {
        this.children.add(child);
    }

    public void setError(String errorKey, String... errorParameters) {
        this.level = ErrorLevel.ERROR;
        this.errorKey = errorKey;
        this.errorParameters = errorParameters;
    }

    public void setWarning(String errorKey, String... errorParameters) {
        this.level = ErrorLevel.WARN;
        this.errorKey = errorKey;
        this.errorParameters = errorParameters;
    }

    /**
     * @return the level
     */
    public ErrorLevel getStatus() {
        return this.level;
    }

    /**
     * @param level the level to set
     */
    public void setStatus(ErrorLevel level) {
        this.level = level;
    }

    /**
     * @return the errorKey
     */
    public String getErrorKey() {
        return this.errorKey;
    }

    /**
     * @param errorKey the errorKey to set
     */
    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }

    /**
     * @return the errorParameters
     */
    public String[] getErrorParameters() {
        return this.errorParameters;
    }

    /**
     * @param errorParameters the errorParameters to set
     */
    public void setErrorParameters(String[] errorParameters) {
        this.errorParameters = errorParameters;
    }

    /**
     * @return the entryName
     */
    public String getEntryName() {
        return this.entryName;
    }

    /**
     * @param entryName the entryName to set
     */
    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    /**
     * @return the attributeName
     */
    public String getAttributeName() {
        return this.attributeName;
    }

    /**
     * @param attributeName the attributeName to set
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * @return the constraintName
     */
    public String getConstraintName() {
        return this.constraintName;
    }

    /**
     * @param constraintName the constraintName to set
     */
    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    /**
     * @return the children
     */
    public List<ConstraintValidationResult> getChildren() {
        return this.children;
    }

    /**
     * @return the constraintLabelKey
     */
    public String getConstraintLabelKey() {
        return this.constraintLabelKey;
    }

    /**
     * @param constraintLabelKey the constraintLabelKey to set
     */
    public void setConstraintLabelKey(String constraintLabelKey) {
        this.constraintLabelKey = constraintLabelKey;
    }

    /**
     * @return the attributePath
     */
    public String getAttributePath() {
        return this.attributePath;
    }

    /**
     * @param attributePath the attributePath to set
     */
    public void setAttributePath(String attributePath) {
        this.attributePath = attributePath;
    }

}
