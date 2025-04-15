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
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response addFavorite(String requestBody) {
    try {
        JSONObject request = new JSONObject(requestBody);
        
        if (!request.has("userId") || !request.has("movieId")) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Missing userId or movieId\"}")
                         .build();
        }

        int userId = request.getInt("userId");
        int movieId = request.getInt("movieId");
        
        JSONObject result = favoriteService.addFavorite(userId, movieId);
        int status = result.getString("status").equals("success") ? 200 : 400;
        
        return Response.status(status)
                     .entity(result.toString())
                     .build();
    } catch (JSONException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                     .entity("{\"error\":\"Invalid request format\"}")
                     .build();
    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                     .entity("{\"error\":\"Server error\"}")
                     .build();
    }
}

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavorites(@QueryParam("userId") int userId) {
        try {
            // Input validation
            if (userId <= 0) {
                return buildErrorResponse(Response.Status.BAD_REQUEST, "Invalid user ID");
            }

            JSONArray favorites = favoriteService.getUserFavorites(userId);
            
            // Return empty array instead of error if no favorites
            return Response.ok(favorites.toString()).build();
            
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("API request failed", e);
            return buildErrorResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "Failed to retrieve favorites"
            );
        }
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        return Response.status(status)
            .entity("{\"error\":\"" + message + "\"}")
            .build();
    }
    
    @DELETE
@Path("/remove")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response removeFavorite(String requestBody) {
    try {
        JSONObject request = new JSONObject(requestBody);
        
        if (!request.has("userId") || !request.has("movieId")) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Missing userId or movieId\"}")
                         .build();
        }

        int userId = request.getInt("userId");
        int movieId = request.getInt("movieId");
        
        JSONObject result = favoriteService.deleteFavorite(userId, movieId);
        
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