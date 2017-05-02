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

import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.kim.api.identity.Person;

import java.util.List;

public class NoteForTest extends PersistableBusinessObjectBase {
    private Person authorUniversal;
    private KualiPercent workRatio;
    private String noteText;
    private List<NoteForTest> subNotes;

    public Person getAuthorUniversal() {
        return authorUniversal;
    }

    public void setAuthorUniversal(Person authorUniversal) {
        this.authorUniversal = authorUniversal;
    }

    public KualiPercent getWorkRatio() {
        return workRatio;
    }

    public void setWorkRatio(KualiPercent workRatio) {
        this.workRatio = workRatio;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public List<NoteForTest> getSubNotes() {
        return subNotes;
    }

    public void setSubNotes(List<NoteForTest> subNotes) {
        this.subNotes = subNotes;
    }
}
