/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.sys.rest.exception;

import org.kuali.kfs.sys.rest.ErrorMessage;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiRequestException extends WebApplicationException {

    public ApiRequestException(String message) {
        super(createResponse(Response.Status.BAD_REQUEST, message));
    }

    public ApiRequestException(String message, ErrorMessage detail) {
        super(createResponse(Response.Status.BAD_REQUEST, message, Arrays.asList(detail)));
    }

    public ApiRequestException(String message, List<ErrorMessage> details) {
        super(createResponse(Response.Status.BAD_REQUEST, message, details));
    }

    public ApiRequestException(Response.Status status, String message, List<ErrorMessage> details) {
        super(createResponse(status, message, details));
    }

    public ApiRequestException(Response.Status status, String message, ErrorMessage detail) {
        super(createResponse(status, message, Arrays.asList(detail)));
    }

    private static Response createResponse(Response.Status status, String message) {
        List<String> messages = new ArrayList<>();
        messages.add(message);
        return createResponse(status, message, null);
    }

    private static Response createResponse(Response.Status status, String message, List<ErrorMessage> details) {
        Map<String, Object> errorsMap = new HashMap<>();
        errorsMap.put("message", message);
        if (details != null) {
            errorsMap.put("details", details);
        }
        return Response.status(status).entity(errorsMap).build();
    }
}
