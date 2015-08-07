package org.kuali.kfs.module.purap.rest;

import org.kuali.kfs.module.purap.service.MyOrdersService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.WebUtilities;
import org.kuali.rice.kim.api.identity.Person;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/myorders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MyOrdersResource {
    protected static volatile MyOrdersService myOrdersService;

    @GET
    public Response getLatestOrdersForCurrentUser(@DefaultValue("4") @QueryParam("count") Integer count, @Context HttpServletRequest request) {
        final Person user = WebUtilities.retrieveUserSession(request).getPerson();
        List<Map<String, Object>> latestOrders = getMyOrdersService().getLatestOrders(user, count);
        return Response.ok(latestOrders).build();
    }

    protected MyOrdersService getMyOrdersService() {
        if (myOrdersService == null) {
            myOrdersService = SpringContext.getBean(MyOrdersService.class);
        }
        return myOrdersService;
    }

}
