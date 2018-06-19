package ru.romanov.mta.servlet;

import ru.romanov.mta.servlet.model.ErrorResponse;

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
        ErrorResponse errorResponse = new ErrorResponse(status.getStatusCode(), message);
        return Response.status(status).entity(errorResponse).build();
    }
}
