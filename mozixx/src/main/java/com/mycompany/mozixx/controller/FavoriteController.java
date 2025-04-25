/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.config.JWT;
import com.mycompany.mozixx.service.FavoriteService;
import static com.mysql.cj.conf.PropertyKey.logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

@Path("/favorites")
public class FavoriteController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FavoriteService.class);
    private final FavoriteService favoriteService = new FavoriteService();

    @POST
    @Path("/add")
    public Response addToFavorites(
        @HeaderParam("Authorization") String authHeader,
        String jsonBody) {
        
        try {
            // 1. Token kinyerése a headerből
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new JSONObject()
                        .put("status", "error")
                        .put("message", "Érvénytelen hitelesítési fejléc"))
                    .build();
            }
            
            String jwtToken = authHeader.substring("Bearer ".length());
            JSONObject request = new JSONObject(jsonBody);
            
            // 2. Kötelező mezők ellenőrzése
            if (!request.has("movieId")) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new JSONObject()
                        .put("status", "error")
                        .put("message", "Hiányzó movieId"))
                    .build();
            }
            
            int movieId = request.getInt("movieId");
            
            // 3. Service hívása
            JSONObject result = favoriteService.addMovieToFavorites(jwtToken, movieId);
            
            return Response.status(result.getInt("statusCode"))
                .entity(result.toString())
                .build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new JSONObject()
                    .put("status", "error")
                    .put("message", "Belső szerverhiba"))
                .build();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavoriteMovies(@HeaderParam("Authorization") String authHeader) {
        try {
            // 1. Token kinyerése a headerből
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new JSONObject()
                                .put("status", "error")
                                .put("message", "Érvénytelen hitelesítési fejléc"))
                        .build();
            }

            String jwtToken = authHeader.substring("Bearer ".length());

            // 2. Service hívása
            JSONObject result = favoriteService.getFavoriteMovies(jwtToken);

            return Response.status(result.getInt("statusCode"))
                    .entity(result.toString())
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new JSONObject()
                            .put("status", "error")
                            .put("message", "Belső szerverhiba"))
                    .build();
        }
    }

private Response buildErrorResponse(Response.Status status, String message) {
    JSONObject error = new JSONObject();
    error.put("error", message);
    return Response.status(status)
        .entity(error.toString())
        .build();
}
    
    @DELETE
@Path("/remove")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response removeFavorite(
        @HeaderParam("Authorization") String authHeader,
        String requestBody) {
    
    try {
        // Validate Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity("{\"error\":\"Missing or invalid authorization token\"}")
                         .build();
        }
        
        String jwt = authHeader.substring("Bearer ".length());
        
        // Validate JWT
        if (JWT.validateJWT(jwt) != 1) {
            return Response.status(Response.Status.UNAUTHORIZED)
                         .entity("{\"error\":\"Invalid token\"}")
                         .build();
        }

        JSONObject request = new JSONObject(requestBody);
        
        if (!request.has("movieId")) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Missing movieId\"}")
                         .build();
        }

        int movieId = request.getInt("movieId");
        
        JSONObject result = favoriteService.deleteFavorite(jwt, movieId);
        
        int status = result.getString("status").equals("success") 
            ? Response.Status.OK.getStatusCode() 
            : Response.Status.NOT_FOUND.getStatusCode();
        
        return Response.status(status)
                     .entity(result.toString())
                     .build();
    } catch (JSONException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                     .entity("{\"error\":\"Invalid request format\"}")
                     .build();
    } catch (Exception e) {
        logger.error("Error removing favorite", e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                     .entity("{\"error\":\"Server error\"}")
                     .build();
    }
}
}