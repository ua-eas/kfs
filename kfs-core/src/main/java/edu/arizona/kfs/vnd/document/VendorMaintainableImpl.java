package edu.arizona.kfs.vnd.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.NoteService;

@SuppressWarnings("deprecation")
public class VendorMaintainableImpl extends org.kuali.kfs.vnd.document.VendorMaintainableImpl {
    private static final long serialVersionUID = 4691307271066839354L;

    private NoteService noteService;

    public NoteService getNoteService() {
        if (noteService == null) {
            noteService = SpringContext.getBean(NoteService.class);
        }
        return noteService;
    }

    @Override
    public void setGenerateDefaultValues(String docTypeName) {
        String objectId = getBusinessObject().getObjectId();
        if (objectId == null) {
            UUID objectUUID = UUID.randomUUID();
            objectId = objectUUID.toString();
            getBusinessObject().setObjectId(objectId);
            Note newBoNote = getNewBoNoteForAdding(VendorConstants.VendorCreateAndUpdateNotePrefixes.ADD);
            getNoteService().save(newBoNote);
        }

        super.setGenerateDefaultValues(docTypeName);
    }

    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        super.processAfterNew(document, requestParameters);
        List<Note> notes = new ArrayList<Note>();
        if (getBusinessObject().getObjectId() != null) {
            notes = getNoteService().getByRemoteObjectId(getBusinessObject().getObjectId());
        }
        document.setNotes(notes);
    }

}
