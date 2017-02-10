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

package org.kuali.kfs.sys.batch;

import org.apache.commons.lang3.StringUtils;

public class DataDictionaryFilteredEntity {
    private String entityName;
    private boolean pdf;
    private boolean workflow;
    private boolean kim;
    private boolean audit;

    public DataDictionaryFilteredEntity() {}

    public DataDictionaryFilteredEntity(String entityName) {
        setFilteredEntity(entityName);
    }

    public void setFilteredEntity(String entityName) {
        this.entityName = entityName;
    }

    public boolean matches(String entityName) {
        return entityName.equals(this.getEntityName());
    }

    public String getEntityName() {
        return entityName;
    }

    public boolean isPdf() {
        return pdf;
    }

    public void setPdf(boolean pdf) {
        this.pdf = pdf;
    }

    public boolean isWorkflow() {
        return workflow;
    }

    public void setWorkflow(boolean workflow) {
        this.workflow = workflow;
    }

    public boolean isKim() {
        return kim;
    }

    public void setKim(boolean kim) {
        this.kim = kim;
    }

    public boolean isAudit() {
        return audit;
    }

    public void setAudit(boolean audit) {
        this.audit = audit;
    }

}
