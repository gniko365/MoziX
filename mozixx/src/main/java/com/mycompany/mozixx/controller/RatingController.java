package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.config.JWT;
import com.mycompany.mozixx.service.RatingService;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

@Path("/ratings")
public class RatingController {

    private RatingService ratingService = new RatingService();

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRating(
            @HeaderParam("Authorization") String authHeader,
            String jsonInput) {
        try {
            // JWT token ellenőrzése
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new Exception("Invalid or missing Authorization header.");
            }

            String jwtToken = authHeader.substring("Bearer ".length()).trim();
            int validationResult = JWT.validateJWT(jwtToken);

            if (validationResult != 1) {
                throw new Exception("Invalid or expired JWT token.");
            }

            // Felhasználó azonosítása a token alapján
            int userId = JWT.getUserIdByToken(jwtToken);

            JSONObject jsonObject = new JSONObject(jsonInput);
            int movieId = jsonObject.getInt("movieId");
            int rating = jsonObject.getInt("rating");
            String review = jsonObject.getString("review");
            
            if (rating < 0 || rating > 5) {
            throw new Exception("Rating must be between 0 and 5.");
            }

            // Rating hozzáadása
            ratingService.addRating(userId, movieId, rating, review);

            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("message", "Rating added successfully.");
            return Response.status(Response.Status.CREATED).entity(response.toString()).build();
            } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse.toString()).build();
            }
        }
}