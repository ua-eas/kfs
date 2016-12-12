package org.kuali.kfs.krad.workflow.attribute;

import com.google.common.collect.Lists;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.kns.service.BusinessObjectMetaDataService;
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.kfs.krad.bo.GlobalBusinessObject;
import org.kuali.kfs.krad.bo.GlobalBusinessObjectDetail;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectExtension;
import org.kuali.kfs.krad.service.KRADServiceLocatorInternal;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.workflow.service.WorkflowAttributePropertyResolutionService;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeString;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class DataDictionarySearchableAttributeTest {
    private DataDictionarySearchableAttribute dataDictionarySearchableAttribute;
    private BusinessObjectMetaDataService businessObjectMetaDataService;
    private WorkflowAttributePropertyResolutionService workflowAttributePropertyResolutionService;

    @Before
    public void setUp() {
        dataDictionarySearchableAttribute = new DataDictionarySearchableAttribute();
        PowerMock.mockStatic(KNSServiceLocator.class);
        PowerMock.mockStatic(KRADServiceLocatorInternal.class);
        businessObjectMetaDataService = EasyMock.createMock(BusinessObjectMetaDataService.class);
        workflowAttributePropertyResolutionService = EasyMock.createMock(WorkflowAttributePropertyResolutionService.class);
    }

    @Test
    @PrepareForTest({KNSServiceLocator.class, ObjectUtils.class, KRADServiceLocatorInternal.class})
    public void testFindAllDocumentAttributesForGlobalBusinessObject() {
        MockPBO pbo1 = new MockPBO("BL","1031400","PSYCHOLOGY");
        MockPBO pbo2 = new MockPBO("LB","0041301","YGOLOHCYSP");
        MockPBO pbo3 = new MockPBO("BL","1234567","GLOBAL CHANGES");
        final MockGlobalBO mockGlobalBO = new MockGlobalBO(pbo1, pbo2, pbo3);
        PowerMock.mockStatic(ObjectUtils.class);

        EasyMock.expect(KNSServiceLocator.getBusinessObjectMetaDataService()).andReturn(businessObjectMetaDataService).times(3);
        EasyMock.expect(KRADServiceLocatorInternal.getWorkflowAttributePropertyResolutionService()).andReturn(workflowAttributePropertyResolutionService).times(3);
        EasyMock.expect(businessObjectMetaDataService.listPrimaryKeyFieldNames(MockPBO.class)).andReturn(Arrays.asList("chartOfAccounts","accountNumber")).times(3);
        expectForDocumentAttribute(pbo1, "chartOfAccounts", "BL");
        expectForDocumentAttribute(pbo1, "accountNumber", "1031400");
        expectForDocumentAttribute(pbo2, "chartOfAccounts", "LB");
        expectForDocumentAttribute(pbo2, "accountNumber", "0041301");
        expectForDocumentAttribute(pbo3, "chartOfAccounts", "BL");
        expectForDocumentAttribute(pbo3, "accountNumber", "1234567");

        PowerMock.replay(KNSServiceLocator.class, KRADServiceLocatorInternal.class, ObjectUtils.class);
        EasyMock.replay(businessObjectMetaDataService, workflowAttributePropertyResolutionService);

        final List<DocumentAttribute> documentAttributes = dataDictionarySearchableAttribute.findAllDocumentAttributesForGlobalBusinessObject(mockGlobalBO);

        Assert.assertEquals("We should have six document attributes back", 6, documentAttributes.size());
        assertDocumentAttributes(documentAttributes, 0, "BL", "1031400");
        assertDocumentAttributes(documentAttributes, 2, "LB", "0041301");
        assertDocumentAttributes(documentAttributes, 4, "BL", "1234567");

        PowerMock.verify(KNSServiceLocator.class, KRADServiceLocatorInternal.class, ObjectUtils.class);
        EasyMock.verify(businessObjectMetaDataService, workflowAttributePropertyResolutionService);
    }

    @Test
    @PrepareForTest({KNSServiceLocator.class, ObjectUtils.class, KRADServiceLocatorInternal.class})
    public void testFindAllDocumentAttributesForGlobalBusinessObject_NoGlobalChanges() {
        final MockGlobalBO mockGlobalBO = new MockGlobalBO();
        PowerMock.mockStatic(ObjectUtils.class);

        PowerMock.replay(KNSServiceLocator.class, KRADServiceLocatorInternal.class, ObjectUtils.class);
        EasyMock.replay(businessObjectMetaDataService, workflowAttributePropertyResolutionService);

        final List<DocumentAttribute> documentAttributes = dataDictionarySearchableAttribute.findAllDocumentAttributesForGlobalBusinessObject(mockGlobalBO);

        Assert.assertEquals("We should have zero document attributes back", 0, documentAttributes.size());

        PowerMock.verify(KNSServiceLocator.class, KRADServiceLocatorInternal.class, ObjectUtils.class);
        EasyMock.verify(businessObjectMetaDataService, workflowAttributePropertyResolutionService);
    }

    @Test
    @PrepareForTest({KNSServiceLocator.class, ObjectUtils.class, KRADServiceLocatorInternal.class})
    public void testFindAllDocumentAttributesForGlobalBusinessObject_NullGlobalChanges() {
        final MockGlobalBONull mockGlobalBONull = new MockGlobalBONull();
        PowerMock.mockStatic(ObjectUtils.class);

        PowerMock.replay(KNSServiceLocator.class, KRADServiceLocatorInternal.class, ObjectUtils.class);
        EasyMock.replay(businessObjectMetaDataService, workflowAttributePropertyResolutionService);

        final List<DocumentAttribute> documentAttributes = dataDictionarySearchableAttribute.findAllDocumentAttributesForGlobalBusinessObject(mockGlobalBONull);

        Assert.assertEquals("We should have zero document attributes back", 0, documentAttributes.size());

        PowerMock.verify(KNSServiceLocator.class, KRADServiceLocatorInternal.class, ObjectUtils.class);
        EasyMock.verify(businessObjectMetaDataService, workflowAttributePropertyResolutionService);
    }

    protected void assertDocumentAttributes(List<DocumentAttribute> documentAttributes, int start, String chartValue, String accountValue) {
        Assert.assertEquals("The "+start+" document attribute should have a name of \"chartOfAccounts\"", "chartOfAccounts", documentAttributes.get(start).getName());
        Assert.assertEquals("The "+start+" document attribute should have a value of \""+chartValue+"\"", chartValue, documentAttributes.get(start).getValue());
        Assert.assertEquals("The "+(start+1)+" document attribute should have a name of \"accountNumber\"", "accountNumber", documentAttributes.get(start+1).getName());
        Assert.assertEquals("The "+(start+1)+" document attribute should have a value of \""+accountValue+"\"", accountValue, documentAttributes.get(start+1).getValue());
    }

    protected void expectForDocumentAttribute(MockPBO pbo, String propertyName, String value) {
        EasyMock.expect(ObjectUtils.getPropertyValue(pbo, propertyName)).andReturn(value);
        DocumentAttributeString.Builder chartDocumentAttribute = DocumentAttributeString.Builder.create(propertyName);
        chartDocumentAttribute.setValue(value);
        EasyMock.expect(workflowAttributePropertyResolutionService.buildSearchableAttribute(MockPBO.class, propertyName, value)).andReturn(chartDocumentAttribute.build());
    }

    @Test
    @PrepareForTest({KNSServiceLocator.class, ObjectUtils.class, KRADServiceLocatorInternal.class})
    public void testGenerateSearchableAttributeFromChange() {
        PowerMock.mockStatic(ObjectUtils.class);

        final MockPBO account = new MockPBO("BL","1031400","PSYCHOLOGY");
        EasyMock.expect(KNSServiceLocator.getBusinessObjectMetaDataService()).andReturn(businessObjectMetaDataService);
        EasyMock.expect(KRADServiceLocatorInternal.getWorkflowAttributePropertyResolutionService()).andReturn(workflowAttributePropertyResolutionService);
        EasyMock.expect(businessObjectMetaDataService.listPrimaryKeyFieldNames(MockPBO.class)).andReturn(Arrays.asList("chartOfAccounts","accountNumber"));
        expectForDocumentAttribute(account, "chartOfAccounts", "BL");
        expectForDocumentAttribute(account, "accountNumber", "1031400");

        PowerMock.replay(KNSServiceLocator.class, KRADServiceLocatorInternal.class, ObjectUtils.class);
        EasyMock.replay(businessObjectMetaDataService, workflowAttributePropertyResolutionService);

        final List<DocumentAttribute> documentAttributes = dataDictionarySearchableAttribute.generateSearchableAttributeFromChange(account);

        Assert.assertEquals("We should have two document attributes back", 2, documentAttributes.size());
        assertDocumentAttributes(documentAttributes, 0, "BL", "1031400");

        PowerMock.verify(KNSServiceLocator.class, KRADServiceLocatorInternal.class, ObjectUtils.class);
        EasyMock.verify(businessObjectMetaDataService, workflowAttributePropertyResolutionService);
    }

    @Test
    @PrepareForTest({KNSServiceLocator.class, ObjectUtils.class, KRADServiceLocatorInternal.class})
    public void testGenerateSearchableAttributeFromChange_NoResults() {
        PowerMock.mockStatic(ObjectUtils.class);

        final MockPBO account = new MockPBO("BL","1031400","PSYCHOLOGY");
        EasyMock.expect(KNSServiceLocator.getBusinessObjectMetaDataService()).andReturn(businessObjectMetaDataService);
        EasyMock.expect(KRADServiceLocatorInternal.getWorkflowAttributePropertyResolutionService()).andReturn(workflowAttributePropertyResolutionService);
        EasyMock.expect(businessObjectMetaDataService.listPrimaryKeyFieldNames(MockPBO.class)).andReturn(Arrays.asList());

        PowerMock.replay(KNSServiceLocator.class, KRADServiceLocatorInternal.class, ObjectUtils.class);
        EasyMock.replay(businessObjectMetaDataService, workflowAttributePropertyResolutionService);

        final List<DocumentAttribute> documentAttributes = dataDictionarySearchableAttribute.generateSearchableAttributeFromChange(account);

        Assert.assertEquals("We should have zero document attributes back", 0, documentAttributes.size());

        PowerMock.verify(KNSServiceLocator.class, KRADServiceLocatorInternal.class, ObjectUtils.class);
        EasyMock.verify(businessObjectMetaDataService, workflowAttributePropertyResolutionService);
    }

    @Test
    @PrepareForTest({KNSServiceLocator.class, ObjectUtils.class, KRADServiceLocatorInternal.class})
    public void testGenerateSearchableAttributeFromChange_DuplicateKeys() {
        PowerMock.mockStatic(ObjectUtils.class);

        final MockPBO account = new MockPBO("BL","1031400","PSYCHOLOGY");
        EasyMock.expect(KNSServiceLocator.getBusinessObjectMetaDataService()).andReturn(businessObjectMetaDataService);
        EasyMock.expect(KRADServiceLocatorInternal.getWorkflowAttributePropertyResolutionService()).andReturn(workflowAttributePropertyResolutionService);
        EasyMock.expect(businessObjectMetaDataService.listPrimaryKeyFieldNames(MockPBO.class)).andReturn(Arrays.asList("chartOfAccounts","accountNumber", "accountNumber"));
        expectForDocumentAttribute(account, "chartOfAccounts", "BL");
        expectForDocumentAttribute(account, "accountNumber", "1031400");

        PowerMock.replay(KNSServiceLocator.class, KRADServiceLocatorInternal.class, ObjectUtils.class);
        EasyMock.replay(businessObjectMetaDataService, workflowAttributePropertyResolutionService);

        final List<DocumentAttribute> documentAttributes = dataDictionarySearchableAttribute.generateSearchableAttributeFromChange(account);

        Assert.assertEquals("We should have two document attributes back", 2, documentAttributes.size());
        assertDocumentAttributes(documentAttributes, 0, "BL", "1031400");

        PowerMock.verify(KNSServiceLocator.class, KRADServiceLocatorInternal.class, ObjectUtils.class);
        EasyMock.verify(businessObjectMetaDataService, workflowAttributePropertyResolutionService);
    }

    class MockPBO implements PersistableBusinessObject {
        private String chartOfAccounts;
        private String accountNumber;
        private String accountName;

        public MockPBO(String chartOfAccounts, String accountNumber, String accountName) {
            this.chartOfAccounts = chartOfAccounts;
            this.accountNumber = accountNumber;
            this.accountName = accountName;
        }

        public String getChartOfAccounts() {
            return chartOfAccounts;
        }

        public void setChartOfAccounts(String chartOfAccounts) {
            this.chartOfAccounts = chartOfAccounts;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        @Override
        public void setVersionNumber(Long versionNumber) {
        }

        @Override
        public void setObjectId(String objectId) {
        }

        public Timestamp getLastUpdatedTimestamp() { return null; }

        public void setLastUpdatedTimestamp(Timestamp lastUpdatedTimestamp) {
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

    class MockGlobalBO implements GlobalBusinessObject {
        private List<PersistableBusinessObject> globalChanges;

        public MockGlobalBO(PersistableBusinessObject... globalChanges) {
            this.globalChanges = Lists.newArrayList(globalChanges);
        }

        @Override
        public String getDocumentNumber() {
            return null;
        }

        @Override
        public void setDocumentNumber(String documentNumber) {
        }

        @Override
        public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
            return globalChanges;
        }

        @Override
        public List<PersistableBusinessObject> generateDeactivationsToPersist() {
            return null;
        }

        @Override
        public boolean isPersistable() {
            return false;
        }

        @Override
        public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
            return null;
        }
    }

    class MockGlobalBONull implements GlobalBusinessObject {
        public MockGlobalBONull() {
        }

        @Override
        public String getDocumentNumber() {
            return null;
        }

        @Override
        public void setDocumentNumber(String documentNumber) {
        }

        @Override
        public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
            return null;
        }

        @Override
        public List<PersistableBusinessObject> generateDeactivationsToPersist() {
            return null;
        }

        @Override
        public boolean isPersistable() {
            return false;
        }

        @Override
        public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
            return null;
        }
    }
}