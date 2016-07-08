package org.kuali.kfs.sys.businessobject;

public class DocumentHeaderData {
    private String documentNumber;
    private String workflowDocumentTypeName;

    public DocumentHeaderData(String documentNumber, String workflowDocumentTypeName) {
        this.documentNumber = documentNumber;
        this.workflowDocumentTypeName = workflowDocumentTypeName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getWorkflowDocumentTypeName() {
        return workflowDocumentTypeName;
    }
}
