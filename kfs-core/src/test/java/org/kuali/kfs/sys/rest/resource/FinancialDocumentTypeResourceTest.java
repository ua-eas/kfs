package org.kuali.kfs.sys.rest.resource;

import org.easymock.EasyMock;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentTypeService;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class FinancialDocumentTypeResourceTest {
    @TestSubject
    FinancialDocumentTypeResource financialDocumentTypeResource = new FinancialDocumentTypeResource();

    @Mock
    FinancialSystemDocumentTypeService financialSystemDocumentTypeService;

    @Mock
    DocumentTypeService documentTypeService;

    DocumentType documentType;
    DocumentType parentDocumentType;

    @Before
    public void setUp() {
        documentType = PowerMock.createMock(DocumentType.class);
        parentDocumentType = PowerMock.createMock(DocumentType.class);
    }

    @Test
    @PrepareForTest(DocumentType.class)
    public void getFinancialDocumentType() throws Exception {
        initializeMocks("ZZZZ", "Snore Doc");
        PowerMock.replay(financialSystemDocumentTypeService, documentTypeService, documentType, parentDocumentType);

        financialDocumentTypeResource.setFinancialSystemDocumentTypeService(financialSystemDocumentTypeService);
        financialDocumentTypeResource.setDocumentTypeService(documentTypeService);

        final Response response = financialDocumentTypeResource.getFinancialDocumentType("ZZZZ");

        PowerMock.verify(financialSystemDocumentTypeService, documentTypeService, documentType, parentDocumentType);
        assertResponse(response);
        assertFinancialDocumentTypeInformationValues(response, "ZZZZ", Boolean.TRUE, Boolean.TRUE,
                "Snore Doc", "AAAA");
    }

    @Test
    @PrepareForTest(DocumentType.class)
    public void getFinancialDocumentType_LowerCase() throws Exception {
        initializeMocks("YYYY", "Yawn Doc");
        PowerMock.replay(financialSystemDocumentTypeService, documentTypeService, documentType, parentDocumentType);

        financialDocumentTypeResource.setFinancialSystemDocumentTypeService(financialSystemDocumentTypeService);
        financialDocumentTypeResource.setDocumentTypeService(documentTypeService);

        final Response response = financialDocumentTypeResource.getFinancialDocumentType("yyyy");

        PowerMock.verify(financialSystemDocumentTypeService, documentTypeService, documentType, parentDocumentType);
        assertResponse(response);
        assertFinancialDocumentTypeInformationValues(response, "YYYY", Boolean.TRUE, Boolean.TRUE,
                "Yawn Doc", "AAAA");
    }

    @Test
    @PrepareForTest(DocumentType.class)
    public void getFinancialDocumentType_MixedCase() throws Exception {
        initializeMocks("WonderDocument", "Wonder Document");
        PowerMock.replay(financialSystemDocumentTypeService, documentTypeService, documentType, parentDocumentType);

        financialDocumentTypeResource.setFinancialSystemDocumentTypeService(financialSystemDocumentTypeService);
        financialDocumentTypeResource.setDocumentTypeService(documentTypeService);

        final Response response = financialDocumentTypeResource.getFinancialDocumentType("WonderDocument");

        PowerMock.verify(financialSystemDocumentTypeService, documentTypeService, documentType, parentDocumentType);
        assertResponse(response);
        assertFinancialDocumentTypeInformationValues(response, "WonderDocument", Boolean.TRUE, Boolean.TRUE,
                "Wonder Document", "AAAA");
    }

    private void initializeMocks(String expectedDocumentTypeName, String expectedDocumentTypeLabel) {
        EasyMock.expect(financialSystemDocumentTypeService.isCurrentActiveAccountingDocumentType(expectedDocumentTypeName)).andReturn(true);
        EasyMock.expect(financialSystemDocumentTypeService.isFinancialSystemDocumentType(expectedDocumentTypeName)).andReturn(true);
        EasyMock.expect(documentTypeService.getDocumentTypeByName(expectedDocumentTypeName)).andReturn(documentType);
        EasyMock.expect(documentType.getLabel()).andReturn(expectedDocumentTypeLabel);
        EasyMock.expect(documentType.getParentId()).andReturn("7332");
        EasyMock.expect(documentTypeService.getDocumentTypeById("7332")).andReturn(parentDocumentType);
        EasyMock.expect(parentDocumentType.getName()).andReturn("AAAA");
    }

    private void assertKeyAndValue(Map<String, Object> configuration, String keyName, Object value) {
        Assert.assertTrue("FinancialDocumentType should have key: \""+keyName+"\"", configuration.containsKey(keyName));
        Assert.assertNotNull("FinancialDocumentType should actually hold value for key: \"" + keyName + "\"", configuration.get(keyName));
        Assert.assertEquals("FinancialDocumentType key: \""+keyName+"\" should have a value of "+value.toString(), value, configuration.get(keyName));
    }

    private void assertResponse(Response response) {
        Assert.assertNotNull("We should have gotten back a response", response);
        Assert.assertEquals("The response code should be 200", 200, response.getStatus());
        Assert.assertNotNull("We should have gotten back an entity", response.getEntity());
        Assert.assertTrue("The entity should be a Map", Map.class.isAssignableFrom(response.getEntity().getClass()));
    }

    private void assertFinancialDocumentTypeInformationValues(Response response, String documentTypeName,
                                                              Boolean isAccountingDocument, Boolean isFinancialDocument,
                                                              String label, String parentDocType) {
        final Map<String, Object> financialDocumentType = (Map<String, Object>)response.getEntity();
        assertKeyAndValue(financialDocumentType, KFSPropertyConstants.DOCUMENT_TYPE_NAME, documentTypeName);
        assertKeyAndValue(financialDocumentType, "currentActiveAccountingDocumentType", isAccountingDocument);
        assertKeyAndValue(financialDocumentType, "financialSystemDocumentType", isFinancialDocument);
        assertKeyAndValue(financialDocumentType, "documentTypeLabel", label);
        assertKeyAndValue(financialDocumentType, "parentDocumentTypeName", parentDocType);
    }
}