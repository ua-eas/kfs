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
package org.kuali.kfs.krad.bo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;


@Entity
@Table(name = "KRNS_LOOKUP_SEL_T")
public class SelectedObjectIds extends MultipleValueLookupMetadata {
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "SEL_OBJ_IDS")
    private String selectedObjectIds;

    public String getSelectedObjectIds() {
        return selectedObjectIds;
    }

    public void setSelectedObjectIds(String selectedObjectIds) {
        this.selectedObjectIds = selectedObjectIds;
    }
}

