package ru.romanov.mtsa.servlet;

import ru.romanov.mtsa.servlet.model.ErrorJson;

import javax.ws.rs.core.Response;

/**
 * Parent servlet class. Contains common methods for all project servlets
 *
 * @author Egor Romanov
 */
public class AbstractServlet {

    protected Response buildSuccessResponse() {
        return Response.status(Response.Status.OK).build();
    }

    protected Response buildSuccessResponse(Object entity) {
        return Response.status(Response.Status.OK).entity(entity).build();
    }

    protected Response buildErrorResponse(Response.Status status) {
        return Response.status(status).build();
    }

    protected Response buildErrorResponse(Response.Status status, String message) {
        ErrorJson errorJson = new ErrorJson(status.getStatusCode(), message);
        return Response.status(status).entity(errorJson).build();
    }
}
