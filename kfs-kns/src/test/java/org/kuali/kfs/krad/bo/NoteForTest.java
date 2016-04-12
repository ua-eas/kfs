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
