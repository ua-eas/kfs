package edu.arizona.kfs.module.purap.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.datadictionary.RoutingTypeDefinition;
import org.kuali.rice.krad.datadictionary.WorkflowAttributes;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorInternal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kew.api.document.node.RouteNodeInstance;




public class RequisitionAccountingLineAuthorizer extends org.kuali.kfs.module.purap.document.authorization.RequisitionAccountingLineAuthorizer {
    
    protected static final String KIM_ATTRIBUTE_DOCUMENT_NUMBER = KimConstants.AttributeConstants.DOCUMENT_NUMBER;

    protected static final String KIM_ATTRIBUTE_DOCUMENT_TYPE_NAME = KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME;

    protected static final String KIM_ATTRIBUTE_ROUTE_LEVEL_NAME = KimConstants.AttributeConstants.ROUTE_NODE_NAME;
    
    protected static final String KIM_ATTRIBUTE_PROPERTY_NAME = KimConstants.AttributeConstants.PROPERTY_NAME;

    protected void addCommonQualifiersToAttributeSet(Map<String, String> qualifier, Document document, DocumentEntry documentEntry, String routeLevel) {
        if (document != null) {
            qualifier.put(KIM_ATTRIBUTE_DOCUMENT_NUMBER, document.getDocumentNumber());
        }
        if (documentEntry != null) {
            qualifier.put(KIM_ATTRIBUTE_DOCUMENT_TYPE_NAME, documentEntry.getDocumentTypeName());
        }
        qualifier.put(KIM_ATTRIBUTE_ROUTE_LEVEL_NAME, routeLevel);
    }

    @Override
    protected boolean allowAccountingLinesAreEditable(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        // the fields in a new line should be always editable
        RequisitionDocument reqDocument = (RequisitionDocument) accountingDocument;
        if (PurapConstants.RequisitionStatuses.APPDOC_AWAIT_FISCAL_REVIEW.equals(reqDocument.getApplicationDocumentStatus()) && accountingLine.getSequenceNumber() == null) {
            return true;
        }
        if (PurapConstants.RequisitionStatuses.APPDOC_AWAIT_FISCAL_REVIEW.equals(reqDocument.getApplicationDocumentStatus()) 
                || PurapConstants.RequisitionStatuses.APPDOC_AWAIT_CHART_REVIEW.equals(reqDocument.getApplicationDocumentStatus()) 
                || PurapConstants.RequisitionStatuses.APPDOC_AWAIT_SUB_ACCT_REVIEW.equals(reqDocument.getApplicationDocumentStatus()) 
                || PurapConstants.RequisitionStatuses.APPDOC_AWAIT_OBJECT_SUB_TYPE_CODE_REVIEW.equals(reqDocument.getApplicationDocumentStatus())) {
            // check presence of errors, if so keep the fields editable
            if (accountingLine.equals(GlobalVariables.getUserSession().retrieveObject(accountingLine.getAccountKey()))) {
                return true;
            }
            IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
            List<RouteNodeInstance> routeNodeInstances = reqDocument.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeInstances();
            String nodeName = routeNodeInstances.get(0).getName();
            Map<String, String> permissionDetails = resolvePermissionQualifiers(nodeName);
            List<Map<String, String>> roleQualifiers = resolveRoleQualifiers(accountingDocument, nodeName);
            if (roleQualifiers != null && !roleQualifiers.isEmpty()) {
                Map<String, String> matchingAccountingLine = findMatchingAccountingLine(accountingLine, roleQualifiers);
                if ((matchingAccountingLine != null && identityManagementService.isAuthorizedByTemplateName(GlobalVariables.getUserSession().getPrincipalId(), KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.PermissionTemplate.MODIFY_ACCOUNTING_LINES.name, permissionDetails, matchingAccountingLine)) || PurapConstants.RequisitionStatuses.APPDOC_AWAIT_OBJECT_SUB_TYPE_CODE_REVIEW.equals(reqDocument.getApplicationDocumentStatus())) {
                    return super.allowAccountingLinesAreEditable(accountingDocument, accountingLine);
                }
            }
            return false;
        }
        return super.allowAccountingLinesAreEditable(accountingDocument, accountingLine);
    }

    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        if (RequisitionDocument.class.isAssignableFrom(accountingDocument.getClass())) {
            IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
            RequisitionDocument reqsDoc = (RequisitionDocument) accountingDocument;
            if (PurapConstants.RequisitionStatuses.APPDOC_AWAIT_OBJECT_SUB_TYPE_CODE_REVIEW.equals(reqsDoc.getApplicationDocumentStatus())) {
                // check presence of errors, if so keep the fields editable
                if (accountingLine.equals(GlobalVariables.getUserSession().retrieveObject(accountingLine.getAccountKey())) && fieldName.equals(PurapPropertyConstants.FINANCIAL_OBJECT_CODE)) {
                    return true;
                }
                Map<String, String> permissionDetails = new HashMap<String, String>();
                permissionDetails.put(KIM_ATTRIBUTE_DOCUMENT_TYPE_NAME, PurapConstants.REQUISITION_DOCUMENT_TYPE);
                permissionDetails.put(KIM_ATTRIBUTE_ROUTE_LEVEL_NAME, PurapConstants.RequisitionStatuses.NODE_OBJECT_SUB_TYPE_CODE);
                String propertyName = KFSPropertyConstants.ITEMS + KFSConstants.DELIMITER + KFSPropertyConstants.SOURCE_ACCOUNTING_LINES + KFSConstants.DELIMITER + fieldName;
                permissionDetails.put(KIM_ATTRIBUTE_PROPERTY_NAME, propertyName);
                List<Map<String, String>> roleQualifiers = resolveRoleQualifiers(accountingDocument, PurapConstants.RequisitionStatuses.NODE_OBJECT_SUB_TYPE_CODE);
                // check the permission here
                if (roleQualifiers != null && !roleQualifiers.isEmpty()) {
                    Map<String, String> matchingAccountingLine = findMatchingAccountingLine(accountingLine, roleQualifiers);
                    if (matchingAccountingLine != null && identityManagementService.isAuthorizedByTemplateName(GlobalVariables.getUserSession().getPrincipalId(), KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.PermissionTemplate.MODIFY_ACCOUNTING_LINES.name, permissionDetails, matchingAccountingLine)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage);
    }

    protected RoutingTypeDefinition getWorkflowAttributeDefintion(DocumentEntry documentEntry, String routeLevelName) {
        final WorkflowAttributes workflowAttributes = documentEntry.getWorkflowAttributes();
        if (workflowAttributes == null) {
            return null;
        }
        final Map<String, RoutingTypeDefinition> routingTypeMap = workflowAttributes.getRoutingTypeDefinitions();
        if (routingTypeMap.containsKey(routeLevelName)) {
            return routingTypeMap.get(routeLevelName);
        }
        return null;
    }

    protected Map<String, String> resolvePermissionQualifiers(String nodeName) {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KIM_ATTRIBUTE_DOCUMENT_TYPE_NAME, PurapConstants.REQUISITION_DOCUMENT_TYPE);
        permissionDetails.put(KIM_ATTRIBUTE_ROUTE_LEVEL_NAME, nodeName);
        String propertyName = KFSPropertyConstants.ITEMS + KFSConstants.DELIMITER + KFSPropertyConstants.SOURCE_ACCOUNTING_LINES;
        permissionDetails.put(KIM_ATTRIBUTE_PROPERTY_NAME, propertyName);
        return permissionDetails;
    }

    private Map<String, String> findMatchingAccountingLine(AccountingLine line, List<Map<String, String>> qualifiers) {
        String chartCode = line.getChartOfAccountsCode();
        String accountNumber = line.getAccountNumber();
        String orgCode = null;
        if(ObjectUtils.isNotNull(line.getAccount())) {
            orgCode = line.getAccount().getOrganizationCode();
        }
        String overrideCode = line.getOverrideCode();
        String objectCode = line.getFinancialObjectCode();
        String subAcctNumber = line.getSubAccountNumber();
        String financialSubObjectCode = line.getFinancialSubObjectCode();
        for (Map<String, String> attributeSet : qualifiers) {
            if ((!attributeSet.containsKey(PurapPropertyConstants.CHART_OF_ACCOUNTS_CODE) || StringUtils.equals(chartCode, attributeSet.get(PurapPropertyConstants.CHART_OF_ACCOUNTS_CODE))) 
                    && (!attributeSet.containsKey(PurapPropertyConstants.ACCOUNT_NUMBER) || StringUtils.equals(accountNumber, attributeSet.get(PurapPropertyConstants.ACCOUNT_NUMBER))) 
                    && (!attributeSet.containsKey(KFSPropertyConstants.OVERRIDE_CODE) || StringUtils.equals(overrideCode, attributeSet.get(KFSPropertyConstants.OVERRIDE_CODE))) 
                    && (!attributeSet.containsKey(PurapPropertyConstants.ORGANIZATION_CODE) || StringUtils.equals(orgCode, attributeSet.get(PurapPropertyConstants.ORGANIZATION_CODE))) 
                    && (!attributeSet.containsKey(PurapPropertyConstants.FINANCIAL_OBJECT_CODE) || StringUtils.equals(objectCode, attributeSet.get(PurapPropertyConstants.FINANCIAL_OBJECT_CODE))) 
                    && (!attributeSet.containsKey(KFSPropertyConstants.SUB_ACCOUNT_NUMBER) || StringUtils.equals(subAcctNumber, attributeSet.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER))) 
                    && (!attributeSet.containsKey(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE) || StringUtils.equals(subAcctNumber, attributeSet.get(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE)))) {
                return attributeSet;
            }
        }
        return null;
    }

    protected List<Map<String, String>> resolveRoleQualifiers(AccountingDocument accountingDocument, String nodeName) {
        DocumentEntry documentEntry = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getDocumentEntry(PurapConstants.REQUISITION_DOCUMENT_TYPE);
        List<Map<String, String>> roleQualifiers = null;
        final RoutingTypeDefinition routingTypeDefinition = getWorkflowAttributeDefintion(documentEntry, nodeName);
        if (accountingDocument != null && routingTypeDefinition != null) {
            roleQualifiers = KRADServiceLocatorInternal.getWorkflowAttributePropertyResolutionService().resolveRoutingTypeQualifiers(accountingDocument, routingTypeDefinition);
        }
        
        for (Map<String, String> qualifier : roleQualifiers) {
            addCommonQualifiersToAttributeSet(qualifier, accountingDocument, documentEntry, nodeName);
        }
        
        return roleQualifiers;
    }

    @Override
    public boolean hasEditPermissionOnAccountingLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, Person currentUser, boolean pageIsEditable) {
        boolean currentUserIsDocumentInitiator = StringUtils.equalsIgnoreCase( accountingDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId(), currentUser.getPrincipalId() );
        if (!determineEditPermissionOnLine(accountingDocument, accountingLine, accountingLineCollectionProperty, currentUserIsDocumentInitiator, pageIsEditable)) {
            if (approvedForUnqualifiedEditing(accountingDocument, accountingLine, accountingLineCollectionProperty, currentUserIsDocumentInitiator)) {
                return true;
            }
            return false;
        }
        return true;
    }
    
}
