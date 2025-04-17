package filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class CORSFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (isPreflightRequest(requestContext)) {
            requestContext.abortWith(Response.ok().build());
        }
    }

    private boolean isPreflightRequest(ContainerRequestContext request) {
        return request.getHeaderString("Origin") != null
                && request.getMethod().equalsIgnoreCase("OPTIONS");
    }

    @Override
    public void filter(ContainerRequestContext requestContext, 
                     ContainerResponseContext responseContext) {
        responseContext.getHeaders().add(
            "Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add(
            "Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add(
            "Access-Control-Allow-Headers",
            "origin, content-type, accept, authorization, x-requested-with");
        responseContext.getHeaders().add(
            "Access-Control-Allow-Methods",
            "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().add(
            "Access-Control-Expose-Headers",
            "content-type, authorization");
    }
}