/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject.dto;

import java.util.HashSet;
import java.util.Set;

public class EntityDTO {

    private String moduleCode;
    private String code;
    private String name;
    private String description;
    private TableDTO rootTable;
    private Set<ConcernDTO> concerns = new HashSet<>();

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TableDTO getRootTable() {
        return rootTable;
    }

    public void setRootTable(TableDTO rootTable) {
        this.rootTable = rootTable;
    }

    public Set<ConcernDTO> getConcerns() {
        return concerns;
    }

    public void setConcerns(Set<ConcernDTO> concerns) {
        this.concerns = concerns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityDTO entityDTO = (EntityDTO) o;

        if (!moduleCode.equals(entityDTO.moduleCode)) return false;
        return code.equals(entityDTO.code);
    }

    @Override
    public int hashCode() {
        int result = moduleCode.hashCode();
        result = 31 * result + code.hashCode();
        return result;
    }
}
