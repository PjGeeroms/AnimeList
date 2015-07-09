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
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;


@Provider
@PreMatching
public class ResponseFilter implements ContainerResponseFilter{

    private final static Logger log = Logger.getLogger(ResponseFilter.class.getName());
    
    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        log.info("Executing REST response filter");
        
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Headers", "Authorization, Content-Type, Origin");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
    }
}
