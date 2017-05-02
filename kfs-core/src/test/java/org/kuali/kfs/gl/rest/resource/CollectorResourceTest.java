package org.kuali.kfs.gl.rest.resource;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.gl.service.CollectorApiService;
import org.kuali.rice.kim.api.permission.PermissionService;

import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectorResourceTest {
    private CollectorResource collectorResource;
    private PermissionService permissionService;
    private CollectorApiService collectorApiService;

    @Before
    public void setUp() {
        permissionService = EasyMock.createMock(PermissionService.class);
        collectorApiService = EasyMock.createMock(CollectorApiService.class);
        collectorResource = new CollectorResource() {
            @Override
            protected String getPrincipalId() {
                return "1234567890";
            }

            @Override
            protected PermissionService getPermissionService() {
                return permissionService;
            }

            @Override
            protected CollectorApiService getCollectorApiService() {
                return collectorApiService;
            }
        };
    }

    @Test
    public void testInvalidContentType() {
        InputStream stream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));

        Response response = collectorResource.postCollectorFile("bad",stream);

        Assert.assertEquals(400,response.getStatus());
    }

    @Test
    public void testFlatFile_NoPermission() {
        InputStream stream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));

        EasyMock.expect(permissionService.hasPermissionByTemplate("1234567890","KR-NS","Upload Batch Input File(s)",getDetails("collectorFlatFileInputFileType"))).andReturn(false);
        EasyMock.replay(permissionService,collectorApiService);

        Response response = collectorResource.postCollectorFile("text/plain",stream);

        EasyMock.verify(permissionService,collectorApiService);
        Assert.assertEquals(401,response.getStatus());
    }

    @Test
    public void testXmlFile_NoPermission() {
        InputStream stream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));

        EasyMock.expect(permissionService.hasPermissionByTemplate("1234567890","KR-NS","Upload Batch Input File(s)",getDetails("collectorXmlInputFileType"))).andReturn(false);
        EasyMock.replay(permissionService,collectorApiService);

        Response response = collectorResource.postCollectorFile("text/xml",stream);

        EasyMock.verify(permissionService,collectorApiService);
        Assert.assertEquals(401,response.getStatus());
    }

    @Test
    public void testXmlFile_Errors() {
        InputStream stream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        List<String> errors = new ArrayList<>();
        errors.add("Error 1");

        EasyMock.expect(permissionService.hasPermissionByTemplate("1234567890","KR-NS","Upload Batch Input File(s)",getDetails("collectorXmlInputFileType"))).andReturn(true);
        EasyMock.expect(collectorApiService.collectorApiLoad(stream,"text/xml")).andReturn(errors);
        EasyMock.replay(permissionService,collectorApiService);

        Response response = collectorResource.postCollectorFile("text/xml",stream);

        EasyMock.verify(permissionService,collectorApiService);
        Assert.assertEquals(400,response.getStatus());
        Map<String,Object> entity = (Map<String,Object>)response.getEntity();
        Assert.assertTrue(entity.containsKey("errors"));
        List<String> retrievedErrors = (List<String>)entity.get("errors");
        Assert.assertEquals(1,retrievedErrors.size());
        Assert.assertEquals("Error 1",retrievedErrors.get(0));
    }

    @Test
    public void testXmlFile_NoErrors() {
        InputStream stream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));

        EasyMock.expect(permissionService.hasPermissionByTemplate("1234567890","KR-NS","Upload Batch Input File(s)",getDetails("collectorXmlInputFileType"))).andReturn(true);
        EasyMock.expect(collectorApiService.collectorApiLoad(stream,"text/xml")).andReturn(new ArrayList<>());
        EasyMock.replay(permissionService,collectorApiService);

        Response response = collectorResource.postCollectorFile("text/xml",stream);

        EasyMock.verify(permissionService,collectorApiService);
        Assert.assertEquals(200,response.getStatus());
    }

    private Map<String,String> getDetails(String beanName) {
        Map<String,String> details = new HashMap<>();
        details.put("namespaceCode","KFS-GL");
        details.put("beanName",beanName);
        return details;
    }
}