/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author PieterJan
 */
@Provider
public class ExceptionHttpStatusResolver implements ExceptionMapper<Throwable>{

    @Override
    public Response toResponse(Throwable exception) {
        Response.Status httpStatus = Response.Status.INTERNAL_SERVER_ERROR;
        
        if (exception instanceof AlreadyFoundException) {
            httpStatus = Response.Status.BAD_REQUEST;
        }
        return Response.status(httpStatus).entity(exception.getMessage()).build();
    }
    
}
