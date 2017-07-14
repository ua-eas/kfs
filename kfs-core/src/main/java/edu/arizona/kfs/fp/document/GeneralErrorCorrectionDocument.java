package edu.arizona.kfs.fp.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.document.validation.event.AccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.DeleteAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.ReviewAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.UpdateAccountingLineEvent;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.fp.businessobject.ErrorCertification;
import edu.arizona.kfs.fp.businessobject.GECSourceAccountingLine;
import edu.arizona.kfs.fp.businessobject.GECTargetAccountingLine;
import edu.arizona.kfs.fp.document.service.EntryGecDocNumUpdaterService;
import edu.arizona.kfs.gl.businessobject.GecEntryRelationship;
import edu.arizona.kfs.sys.KFSConstants;


public class GeneralErrorCorrectionDocument extends org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument {
    private static final long serialVersionUID = 3559591546723165167L;
    private static final Set<String> GEC_ACTIVE_ROUTE_STATUS_CODES = KFSConstants.GEC_ACTIVE_ROUTE_STATUS_CODES;

    private Set<GecEntryRelationship> gecEntryRelationships;
    private ErrorCertification errorCertification;
    private Integer errorCertID;
    private DebitDeterminerService debitService;
    private EntryGecDocNumUpdaterService entryGecDocNumUpdaterService;



    public void toCopy() throws WorkflowException {
        super.toCopy();

        errorCertID = null;
        ErrorCertification oldErrorCertification = errorCertification;
        errorCertification = new ErrorCertification();
        errorCertification.setExpenditureDescription(oldErrorCertification.getExpenditureDescription());
        errorCertification.setExpenditureProjectBenefit(oldErrorCertification.getExpenditureProjectBenefit());
        errorCertification.setErrorDescription(oldErrorCertification.getErrorDescription());
        errorCertification.setErrorCorrectionReason(oldErrorCertification.getErrorCorrectionReason());
    }


    public Entry getEntryByAccountingLine(AccountingLine line) {
        if (line != null) {
            return getEntryMap().get(line.getObjectId());
        }

        return null;
    }


    /*
     * Overridden to deal w/ Entry and GecEntryRelatioship association handeling
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        updateEntriesForRelationshipChange();
        resequenceLinesAndRelationships();
    }


    /*
     * Resquence the lines, but also keep GecEntryRelationship's up to date with seq changes
     */
    @SuppressWarnings("unchecked")//AccountingLine
    public void resequenceLinesAndRelationships() {

        // Temporarily preserve line-to-relationship map, need to set a custom
        // comparator, otherwise the default PKs cause collisions
        Map<AccountingLine, GecEntryRelationship> sourceLineToRelMap = new TreeMap<>(new Comparator<AccountingLine>(){
            @Override
            public int compare(AccountingLine line, AccountingLine otherLine) {
                return line.getObjectId() == null ? -1 : (line.getObjectId().compareTo(otherLine.getObjectId()));
            }
        });

        for (Object o : getSourceAccountingLines()) {
            AccountingLine line = (AccountingLine) o;
            GecEntryRelationship rel = getRelationshipByLine(line);
            if (rel != null) {
                // Only source lines have a relationships, as target lines don't have their GLE
                // yet (has to be approved, then put through batch, where the GLPE will be processed).
                sourceLineToRelMap.put(line, rel);
            }
        }

        // Now sequence the source lines
        int newIndex = 1;
        for (Object line : getSourceAccountingLines()) {
            ((AccountingLine) line).setSequenceNumber(newIndex++);
        }
        super.setNextSourceLineNumber(newIndex);

        // Next the target lines
        newIndex = 1;
        for (Object line : getTargetAccountingLines()) {
            ((AccountingLine) line).setSequenceNumber(newIndex++);
        }
        super.setNextTargetLineNumber(newIndex);

        // Now we can set the relationships w/ the lines' states. Note: the relationship collection
        // is managed by the framework -- we operate on the collection, the framework will
        // automatically do our updates/deletes. (In fact, using the BOS here will get us an
        // OptimisticLockException and tank.)
        String docRouteStatusCode = this.getDocumentHeader().getWorkflowDocument().getStatus().getCode();
        if (GEC_ACTIVE_ROUTE_STATUS_CODES.contains(docRouteStatusCode)) {
            for (AccountingLine line : sourceLineToRelMap.keySet()) {
                GecEntryRelationship rel = sourceLineToRelMap.get(line);
                rel.setGecAcctLineSeqNumber(line.getSequenceNumber());
                rel.setGecDocRouteStatus(docRouteStatusCode);
                rel.setGecAcctLineObjectId(line.getObjectId());
                LOG.debug(String.format("Updated GecEntryRelationship (entryId, docNum, lineSeqNum, debitCreditCode, lineObjId): (%d, %s, %s, %s, %s)", rel.getEntryId(), rel.getGecDocumentNumber(), rel.getGecAcctLineSeqNumber(), rel.getGecFdocLineTypeCode(), rel.getGecAcctLineObjectId()));
            }
        } else {
            // This went to a status that should release all GLEs, so we should remove the relationships too.
            // Note: This includes the "recall+cancel" scenario, where no lines are deleted, and the relationships
            //       still need to be cleared. Likewise would be the case for "disapprove" action.
            getGecEntryRelationships().clear();
            LOG.debug(String.format("Deleted all GecEntryRelationships for docNum: '%s' going to status '%s'", getDocumentNumber(), docRouteStatusCode));
        }

    }


    /*
     * Overridden to use objectId as map key instead of lineNum+lineType; this was
     * necessary since the default behavior didn't handle resequencing line numbers between
     * saves and submits correctly.
     *
     * Note also: This method is copied from super, only changing how the map
     *            keys were generated (and a little cleanup).
     */
    @Override
    protected List generateEvents(List persistedLines, List currentLines, String errorPathPrefix, TransactionalDocument document) {
        @SuppressWarnings("unchecked")// persistedLines is not parameterized from super
        Map<String, AccountingLine> persistedLineMap = buildLineMap(persistedLines);
        List<AccountingLineEvent> lineEvents = new ArrayList<>();

        // (iterate through current lines to detect additions and updates, removing affected lines from persistedLineMap as we go
        // so deletions can be detected by looking at whatever remains in persistedLineMap)
        int index = 0;
        for (Iterator i = currentLines.iterator(); i.hasNext(); index++) {
            String indexedErrorPathPrefix = errorPathPrefix + "[" + index + "]";
            AccountingLine currentLine = (AccountingLine) i.next();
            String key = currentLine.getObjectId();

            AccountingLine persistedLine = persistedLineMap.get(key);
            // if line is both current and persisted...
            if (persistedLine != null) {
                // ...check for updates
                if (!currentLine.isLike(persistedLine)) {
                    UpdateAccountingLineEvent updateEvent = new UpdateAccountingLineEvent(indexedErrorPathPrefix, document, persistedLine, currentLine);
                    lineEvents.add(updateEvent);
                } else {
                    ReviewAccountingLineEvent reviewEvent = new ReviewAccountingLineEvent(indexedErrorPathPrefix, document, currentLine);
                    lineEvents.add(reviewEvent);
                }

                persistedLineMap.remove(key);
            } else {
                // it must be a new addition
                AddAccountingLineEvent addEvent = new AddAccountingLineEvent(indexedErrorPathPrefix, document, currentLine);
                lineEvents.add(addEvent);
            }
        }

        // detect deletions
        for (Iterator i = persistedLineMap.entrySet().iterator(); i.hasNext(); ) {
            // the deleted line is not displayed on the page, so associate the error with the whole group
            String groupErrorPathPrefix = errorPathPrefix + org.kuali.kfs.sys.KFSConstants.ACCOUNTING_LINE_GROUP_SUFFIX;
            Map.Entry e = (Map.Entry) i.next();
            AccountingLine persistedLine = (AccountingLine) e.getValue();
            DeleteAccountingLineEvent deleteEvent = new DeleteAccountingLineEvent(groupErrorPathPrefix, document, persistedLine, true);
            lineEvents.add(deleteEvent);
        }

        return lineEvents;
    }


    // Overridden to let framework deal with changes to the relationships
    @SuppressWarnings("unchecked")//List<?> from super
    @Override
    public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
        List managedCollections = super.buildListOfDeletionAwareLists();
        managedCollections.add(this.getGecEntryRelationships());

        return managedCollections;
    }


    /*
     * GLPE generation service calls this on parent, but it does not handle the
     * unnatural balance cases corectly (resulting GLPEs have their debit/credit
     * flags always flipped to the natural balance 'normal' state).
     *
     * Since we know the source line's debit/credit flag is directly from a GLE but
     * flipped, and we also know the target line's debit/credit flag is then directly
     * from the source line's debit credit flag but flipped, then we have a transitive
     * chain that tells us exactly what each flag should be along the way, without
     * having to consult any other properties of the GLE or converted lines (aside from
     * what the original GLE debit/credit flag is). This means we only need really
     * simple logic to decide our debit/credit flag.
     *
     * @see org.kuali.kfs.fp.document.CapitalAssetInformationDocumentBase#isDebit()
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return getDebitService().isDebitCode(((AccountingLine) postable).getDebitCreditCode());
    }


    // Overridden to resequence lines, and then sync those to the relationships
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        updateEntriesForRelationshipChange();
        resequenceLinesAndRelationships();
        super.prepareForSave(event);
    }


    // This is how we associate from line->Entry, while also returning all associated entries
    public Map<String, Entry> getEntryMap() {
        Map<String, Entry> entries = new TreeMap<String, Entry>();
        if (getGecEntryRelationships() != null && !getGecEntryRelationships().isEmpty()) {
            for (GecEntryRelationship rel : getGecEntryRelationships()) {
                entries.put(rel.getGecAcctLineObjectId(), rel.getEntry());
            }
        }

        return entries;
    }


    // We can't override super.buildAccountingLineMap(), due to the differeing method signatures
    private Map<String, AccountingLine> buildLineMap(List<AccountingLine> accountingLines) {
        Map<String, AccountingLine> objIdToLineMap = new HashMap<>();
        for (AccountingLine line : accountingLines) {
            objIdToLineMap.put(line.getObjectId(), line);
        }

        return objIdToLineMap;
    }


    private GecEntryRelationship getRelationshipByLine(AccountingLine line) {
        if (line != null && line.getObjectId() != null) {
            for (GecEntryRelationship rel : getGecEntryRelationships()) {
                if (line.getObjectId().equals(rel.getGecAcctLineObjectId())) {
                    return rel;
                }
            }
        }

        return null;
    }


    // Not using BOS in order to avert risk of changing non-GEC fields
    public void updateEntryGecDocNums(Collection<Entry> entryCollection) {
        getEntryGecDocNumUpdaterService().updateEntryGecDocNums(entryCollection);
    }


    // This will "stamp/unstamp" GLEs for when a GecEntryRelationship potentially associates/dissociate
    private void updateEntriesForRelationshipChange() {
        String docRouteStatusCode = this.getDocumentHeader().getWorkflowDocument().getStatus().getCode();

        // Go through entries and either associate or dissassociate the doc from the entry
        List<Entry> entriesToUpdate = new ArrayList<>();
        for (GecEntryRelationship rel : getGecEntryRelationships()) {

            Entry entry = rel.getEntry();
            if (GEC_ACTIVE_ROUTE_STATUS_CODES.contains(docRouteStatusCode)) {
                // The status warrants locking the Entry to this doc
                entry.setGecDocumentNumber(rel.getGecDocumentNumber());
            } else {
                // Doc has been cancelled or disapproved, need to dissociate entry from doc
                entry.setGecDocumentNumber(null);
            }

            entriesToUpdate.add(entry);
        }

        if (entriesToUpdate.size() > 0) {
            updateEntryGecDocNums(entriesToUpdate);
        }

    }


    // Overridden to return AZ verison of the source lines
    @Override
    public Class getSourceAccountingLineClass() {
        return GECSourceAccountingLine.class;
    }


    // Overridden to return AZ version of target lines
    @Override
    public Class getTargetAccountingLineClass() {
        return GECTargetAccountingLine.class;
    }


    public Set<GecEntryRelationship> getGecEntryRelationships() {
        if (ObjectUtils.isNull(gecEntryRelationships)) {
            gecEntryRelationships = new HashSet<>();
        }

        return gecEntryRelationships;
    }


    public void setGecEntryRelationships(Set<GecEntryRelationship> gecEntryRelationships) {
        this.gecEntryRelationships = gecEntryRelationships;
    }


    public ErrorCertification getErrorCertification() {
        return errorCertification;
    }


    public void setErrorCertification(ErrorCertification errorCertification) {
        this.errorCertification = errorCertification;
    }


    public Integer getErrorCertID() {
        return errorCertID;
    }


    public void setErrorCertID(Integer errorCertID) {
        this.errorCertID = errorCertID;
        this.errorCertification.setErrorCertID(errorCertID);
    }


    private DebitDeterminerService getDebitService() {
        if (debitService == null) {
            debitService = SpringContext.getBean(DebitDeterminerService.class);
        }
        return debitService;
    }


    private EntryGecDocNumUpdaterService getEntryGecDocNumUpdaterService() {
        if (entryGecDocNumUpdaterService == null) {
            entryGecDocNumUpdaterService = SpringContext.getBean(EntryGecDocNumUpdaterService.class);
        }
        return entryGecDocNumUpdaterService;
    }

}
