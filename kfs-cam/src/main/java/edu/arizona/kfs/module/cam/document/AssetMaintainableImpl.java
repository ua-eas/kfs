package edu.arizona.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.sys.KFSConstants;

@SuppressWarnings("deprecation")
public class AssetMaintainableImpl extends org.kuali.kfs.module.cam.document.AssetMaintainableImpl {

    private static final long serialVersionUID = 5710219294064590806L;

    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterEdit(document, parameters);
        List<Note> notes = new ArrayList<Note>();
        if (getBusinessObject().getObjectId() != null) {
            NoteService noteService = KRADServiceLocator.getNoteService();
            notes = noteService.getByRemoteObjectId(getBusinessObject().getObjectId());
        }

        setAssetCreateAndUpdateNote(document, notes, KFSConstants.CreateAndUpdateNotePrefixes.CHANGE);
//        document.setNotes(notes);

    }

    @Override
    public void setGenerateDefaultValues(String docTypeName) {
        super.setGenerateDefaultValues(docTypeName);

        if (getBusinessObject().getObjectId() != null) {
            NoteService noteService = KRADServiceLocator.getNoteService();
            List<Note> notes = noteService.getByRemoteObjectId(getBusinessObject().getObjectId());

            if (notes.isEmpty()) {
                Note newBONote = generateNewBoNote(KFSConstants.CreateAndUpdateNotePrefixes.ADD);
                notes.add(newBONote);
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

        setAssetCreateAndUpdateNote(document, notes, KFSConstants.CreateAndUpdateNotePrefixes.ADD);

//        document.setNotes(notes);
    }

    /**
     * Checks whether the previous note was an "Add" with the same document number as this one
     *
     * @param prefix
     *            String to determine if it is a note "Add" or a note "Change"
     */
    private void setAssetCreateAndUpdateNote(MaintenanceDocument document, List<Note> notes, String prefix) {
        boolean shouldAddNote = true;
        System.out.println(getDocumentNumber());
        if (prefix.equals(KFSConstants.CreateAndUpdateNotePrefixes.CHANGE) && (!notes.isEmpty())) {
            // Check whether the previous note was an "Add" with the same document number as this one
            Note previousNote = notes.get(notes.size() - 1);

            if(searchNotesForDocument(notes)&&(previousNote.getNoteText().equals(asset.getBoNotes().get(asset.getBoNotes().size()-1)))) {
//                if(searchNotesForDocument(notes)&&(notes.getNoteText().contains(getDocumentNumber()))) {
                shouldAddNote = false;
             }
        };
        Note newBONote = generateNewBoNote(prefix);
        if (shouldAddNote) {
            notes.add(newBONote);
        }

        
        document.setNotes(notes);
    }

    private boolean searchNotesForDocument(List<Note> notes) {
        for (Note note : notes) {
            if (note.getNoteText().equals(asset.getBoNotes().get(asset.getBoNotes().size()-1))) {
                return true;
            }
        }
        return false;
    }

    protected Note generateNewBoNote(String prefix) {
        Note newBoNote = new Note();
        newBoNote.setNoteText(prefix + " vendor document ID " + getDocumentNumber());
//        previousDocumentNumber = getDocumentNumber();
        newBoNote.setNotePostedTimestampToCurrent();
        newBoNote = SpringContext.getBean(NoteService.class).createNote(newBoNote, getBusinessObject(), GlobalVariables.getUserSession().getPrincipalId());

        return newBoNote;
    }
}
