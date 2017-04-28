package edu.arizona.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.KRADServiceLocator;
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
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterEdit(document, parameters);
        List<Note> notes = new ArrayList<Note>();
        if (getBusinessObject().getObjectId() != null) {
            NoteService noteService = KRADServiceLocator.getNoteService();
            notes = noteService.getByRemoteObjectId(getBusinessObject().getObjectId());
        }

        Note newBoNote = generateChangeBoNote();
        notes.add(newBoNote);
        document.setNotes(notes);

    }

    @Override
    public void setGenerateDefaultValues(String docTypeName) {
        super.setGenerateDefaultValues(docTypeName);

        if (getBusinessObject().getObjectId() != null) {
            NoteService noteService = KRADServiceLocator.getNoteService();
            List<Note> notes = noteService.getByRemoteObjectId(getBusinessObject().getObjectId());

            if (notes.isEmpty()) {
                Note newBoNote = generateAddBoNote();
                notes.add(newBoNote);
            }
        }
    }

    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.setupNewFromExisting(document, parameters);

        List<Note> notes = new ArrayList<Note>();

        if (getBusinessObject().getObjectId() != null) {
            NoteService noteService = KRADServiceLocator.getNoteService();
            notes = noteService.getByRemoteObjectId(getBusinessObject().getObjectId());
        }

        Note newBoNote = generateAddBoNote();
        notes.add(newBoNote);

        document.setNotes(notes);
    }

    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);
        WorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();
        if (workflowDocument != null && workflowDocument.isCanceled()) {
            List<Note> notes = new ArrayList<Note>();
            if (getBusinessObject().getObjectId() != null) {
                notes.addAll(getNoteService().getByRemoteObjectId(getBusinessObject().getObjectId()));
            }

            Note note = generateCancelBoNote();
            getNoteService().save(note);
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

    private Note generateCancelBoNote() {
        Note newBoNote = new Note();
        newBoNote.setNoteText("Cancelled asset document ID " + getDocumentNumber());
        newBoNote.setNotePostedTimestampToCurrent();
        // TODO: Because this is being done by the post-processor, it will be "KFS System User". This needs to be changed to the initiator of the maintenance document.
        String principalId = GlobalVariables.getUserSession().getPrincipalId();
        newBoNote = getNoteService().createNote(newBoNote, getBusinessObject(), principalId);

        return newBoNote;
    }

    // /**
    // * Checks whether the previous note was an "Add" with the same document number as this one
    // *
    // * @param prefix
    // * String to determine if it is a note "Add" or a note "Change"
    // */
    // private void setAssetCreateAndUpdateNote(MaintenanceDocument document, List<Note> notes, String prefix) {
    // boolean shouldAddNote = true;
    //
    // if (prefix.equals(KFSConstants.CreateAndUpdateNotePrefixes.CHANGE) && (!notes.isEmpty())) {
    // // Check whether the previous note was an "Add" with the same document number as this one
    // Note previousNote = notes.get(notes.size() - 1);
    //
    // if (searchNotesForDocument(notes) && (previousNote.getNoteText().contains(getDocumentNumber()))) {
    // shouldAddNote = false;
    // }
    // }
    // if (shouldAddNote) {
    // Note newBONote = generateNewBoNote(prefix);
    // notes.add(newBONote);
    // }
    //
    // }

    // private boolean searchNotesForDocument(List<Note> notes) {
    // for (Note note : notes) {
    // if (note.getNoteText().equals(asset.getBoNotes().get(asset.getBoNotes().size() - 1))) {
    // return true;
    // }
    // }
    // return false;
    // }

    // private Note generateNewBoNote(String prefix) {
    // Note newBoNote = new Note();
    // newBoNote.setNoteText(prefix + " vendor document ID " + getDocumentNumber());
    // newBoNote.setNotePostedTimestampToCurrent();
    // newBoNote = SpringContext.getBean(NoteService.class).createNote(newBoNote, getBusinessObject(), GlobalVariables.getUserSession().getPrincipalId());
    //
    // return newBoNote;
    // }

}
