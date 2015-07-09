/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.io.IOException;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author PieterJan
 */
@Provider
@PreMatching
public class RequestFilter implements ContainerRequestFilter {

    private final static Logger log = Logger.getLogger(ResponseFilter.class.getName());
    
    
    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        log.info("Executing REST request filter");
        if (request.getRequest().getMethod().equals("OPTIONS")) {
            log.info("HTTP METHOD (OPTIONS) - DETECTED!");
            
            request.abortWith(Response.status(Response.Status.OK).build());
        }
    }
    
}
