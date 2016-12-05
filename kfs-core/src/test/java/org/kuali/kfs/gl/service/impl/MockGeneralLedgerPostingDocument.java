/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.gl.service.impl;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.krad.bo.AdHocRoutePerson;
import org.kuali.kfs.krad.bo.AdHocRouteWorkgroup;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectExtension;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.kfs.krad.util.NoteType;
import org.kuali.kfs.krad.util.documentserializer.PropertySerializabilityEvaluator;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocument;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public class MockGeneralLedgerPostingDocument implements GeneralLedgerPostingDocument {
    private List<GeneralLedgerPendingEntry> glpes;

    @Override
    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return glpes;
    }

    @Override
    public GeneralLedgerPendingEntry getGeneralLedgerPendingEntry(int index) {
        return null;
    }

    @Override
    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> generalLedgerPendingEntries) {
        this.glpes = generalLedgerPendingEntries;
    }

    @Override
    public List<SufficientFundsItem> checkSufficientFunds() {
        return null;
    }

    @Override
    public List<GeneralLedgerPendingEntry> getPendingLedgerEntriesForSufficientFundsChecking() {
        return glpes;
    }

    @Override
    public Integer getPostingYear() {
        return null;
    }

    @Override
    public void setPostingYear(Integer postingYear) {

    }

    @Override
    public String getPostingPeriodCode() {
        return null;
    }

    @Override
    public void setPostingPeriodCode(String postingPeriodCode) {

    }

    @Override
    public AccountingPeriod getAccountingPeriod() {
        return null;
    }

    @Override
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {

    }

    @Override
    public String getApplicationDocumentStatus() {
        return null;
    }

    @Override
    public void setApplicationDocumentStatus(String applicationDocumentStatus) {

    }

    @Override
    public void updateAndSaveAppDocStatus(String applicationDocumentStatus) throws WorkflowException {

    }

    @Override
    public FinancialSystemDocumentHeader getFinancialSystemDocumentHeader() {
        return null;
    }

    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        return false;
    }

    @Override
    public DocumentHeader getDocumentHeader() {
        return null;
    }

    @Override
    public void setDocumentHeader(DocumentHeader documentHeader) {

    }

    @Override
    public String getDocumentNumber() {
        return null;
    }

    @Override
    public void setDocumentNumber(String documentHeaderId) {

    }

    @Override
    public void populateDocumentForRouting() {

    }

    @Override
    public String serializeDocumentToXml() {
        return null;
    }

    @Override
    public String getXmlForRouteReport() {
        return null;
    }

    @Override
    public void doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) {

    }

    @Override
    public void doActionTaken(ActionTakenEvent event) {

    }

    @Override
    public void afterActionTaken(ActionType performed, ActionTakenEvent event) {

    }

    @Override
    public void afterWorkflowEngineProcess(boolean successfullyProcessed) {

    }

    @Override
    public void beforeWorkflowEngineProcess() {

    }

    @Override
    public List<String> getWorkflowEngineDocumentIdsToLock() {
        return null;
    }

    @Override
    public String getDocumentTitle() {
        return null;
    }

    @Override
    public List<AdHocRoutePerson> getAdHocRoutePersons() {
        return null;
    }

    @Override
    public List<AdHocRouteWorkgroup> getAdHocRouteWorkgroups() {
        return null;
    }

    @Override
    public void setAdHocRoutePersons(List<AdHocRoutePerson> adHocRoutePersons) {

    }

    @Override
    public void setAdHocRouteWorkgroups(List<AdHocRouteWorkgroup> adHocRouteWorkgroups) {

    }

    @Override
    public void prepareForSave() {

    }

    @Override
    public void validateBusinessRules(KualiDocumentEvent event) {

    }

    @Override
    public void prepareForSave(KualiDocumentEvent event) {

    }

    @Override
    public void postProcessSave(KualiDocumentEvent event) {

    }

    @Override
    public void processAfterRetrieve() {

    }

    @Override
    public boolean getAllowsCopy() {
        return false;
    }

    @Override
    public List<KualiDocumentEvent> generateSaveEvents() {
        return null;
    }

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {

    }

    @Override
    public NoteType getNoteType() {
        return null;
    }

    @Override
    public PersistableBusinessObject getNoteTarget() {
        return null;
    }

    @Override
    public void addNote(Note note) {

    }

    @Override
    public List<Note> getNotes() {
        return null;
    }

    @Override
    public void setNotes(List<Note> notes) {

    }

    @Override
    public Note getNote(int index) {
        return null;
    }

    @Override
    public boolean removeNote(Note note) {
        return false;
    }

    @Override
    public List<String> getLockClearningMethodNames() {
        return null;
    }

    @Override
    public String getBasePathToDocumentDuringSerialization() {
        return null;
    }

    @Override
    public PropertySerializabilityEvaluator getDocumentPropertySerizabilityEvaluator() {
        return null;
    }

    @Override
    public Object wrapDocumentWithMetadataForXmlSerialization() {
        return null;
    }

    @Override
    public void setVersionNumber(Long versionNumber) {

    }

    @Override
    public void setObjectId(String objectId) {

    }

    @Override
    public Timestamp getModifyDate() { return null; }

    @Override
    public void setModifyDate(Timestamp modifyDate) {
    }

    @Override
    public PersistableBusinessObjectExtension getExtension() {
        return null;
    }

    @Override
    public void setExtension(PersistableBusinessObjectExtension extension) {

    }

    @Override
    public void refreshNonUpdateableReferences() {

    }

    @Override
    public void refreshReferenceObject(String referenceObjectName) {

    }

    @Override
    public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
        return null;
    }

    @Override
    public boolean isNewCollectionRecord() {
        return false;
    }

    @Override
    public void setNewCollectionRecord(boolean isNewCollectionRecord) {

    }

    @Override
    public void linkEditableUserFields() {

    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public Long getVersionNumber() {
        return null;
    }

    @Override
    public void refresh() {

    }
}
