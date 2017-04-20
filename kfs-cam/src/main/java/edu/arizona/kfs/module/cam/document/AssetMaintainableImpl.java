package edu.arizona.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;

@SuppressWarnings("deprecation")
public class AssetMaintainableImpl extends org.kuali.kfs.module.cam.document.AssetMaintainableImpl {
    private static final long serialVersionUID = 5710219294064590806L;

    private static NoteService noteService;

    private NoteService getNoteService() {
        if (noteService == null) {
            noteService = SpringContext.getBean(NoteService.class);
        }
        return noteService;
    }

    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);
        WorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();
        if (workflowDocument != null && workflowDocument.isApproved()) {
            List<Note> notes = new ArrayList<Note>();
            if (getBusinessObject().getObjectId() != null) {
                notes.addAll(getNoteService().getByRemoteObjectId(getBusinessObject().getObjectId()));
            }

            boolean isAddNote = isFirstAutoNote(notes);

            if (isAddNote) {
                Note note = generateAddBoNote();
                getNoteService().save(note);
            } else {
                Note note = generateChangeBoNote();
                getNoteService().save(note);
            }
        }

    };

    private Note generateAddBoNote() {
        Note newBoNote = new Note();
        newBoNote.setNoteText("Add asset document ID " + getDocumentNumber());
        newBoNote.setNotePostedTimestampToCurrent();
        // TODO: Because this is being done by the post-processor, it will be "KFS System User". This needs to be changed to the initiator of the maintenance document.
        String principalId = GlobalVariables.getUserSession().getPrincipalId();
        newBoNote = getNoteService().createNote(newBoNote, getBusinessObject(), principalId);

        return newBoNote;
    }

    private Note generateChangeBoNote() {
        Note newBoNote = new Note();
        newBoNote.setNoteText("Change asset document ID " + getDocumentNumber());
        newBoNote.setNotePostedTimestampToCurrent();
        // TODO: Because this is being done by the post-processor, it will be "KFS System User". This needs to be changed to the initiator of the maintenance document.
        String principalId = GlobalVariables.getUserSession().getPrincipalId();
        newBoNote = getNoteService().createNote(newBoNote, getBusinessObject(), principalId);

        return newBoNote;
    }

    private boolean isFirstAutoNote(List<Note> notes) {
        if (notes.size() == 0) {
            return true;
        }

        return false;
    }
}
