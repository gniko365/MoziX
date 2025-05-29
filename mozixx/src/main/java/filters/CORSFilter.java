package filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class CORSFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private final static Logger log = Logger.getLogger(CORSFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        log.info("Filtering request context");
        if (isPreflightRequest(requestContext)) {
            log.info("Preflight request detected, aborting with CORS headers");

            Response.ResponseBuilder builder = Response.ok();
            builder.header("Access-Control-Allow-Origin", requestContext.getHeaderString("Origin"));
            builder.header("Access-Control-Allow-Credentials", "true");
            builder.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with, X-Password, token");
            builder.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

            requestContext.abortWith(builder.build());
        }
    }

    private boolean isPreflightRequest(ContainerRequestContext request) {
        return request.getHeaderString("Origin") != null
                && request.getMethod().equalsIgnoreCase("OPTIONS");
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        log.info("Filtering response context");

        String origin = requestContext.getHeaderString("Origin");
        if (origin != null) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
        } else {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        }

        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization, x-requested-with, X-Password, token");
        responseContext.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().add("Access-Control-Expose-Headers",
                "content-type, authorization");
    }
}
