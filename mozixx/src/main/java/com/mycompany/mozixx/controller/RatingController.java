package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.config.JWT;
import com.mycompany.mozixx.service.FavoriteService;
import com.mycompany.mozixx.service.RatingService;
import static com.mysql.cj.conf.PropertyKey.logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

@Path("/ratings")
public class RatingController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FavoriteService.class);
    private RatingService ratingService = new RatingService();

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRating(String jsonInput) {
        try {
            JSONObject jsonObject = new JSONObject(jsonInput);
            int userId = jsonObject.getInt("userId");  // Now expecting userId in the request body
            int movieId = jsonObject.getInt("movieId");
            int rating = jsonObject.getInt("rating");
            String review = jsonObject.getString("review");
            
            if (rating < 0 || rating > 5) {
                throw new Exception("Rating must be between 0 and 5.");
            }

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

    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRating(String jsonInput) {
        try {
            JSONObject jsonObject = new JSONObject(jsonInput);
            int userId = jsonObject.getInt("userId");  // Now expecting userId in the request body
            int ratingId = jsonObject.getInt("ratingId");

            ratingService.deleteRatingById(ratingId, userId);

            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("message", "Rating deleted successfully.");
            return Response.status(Response.Status.OK).entity(response.toString()).build();
        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse.toString()).build();
        }
    }
    
    @GET
@Path("/list")
@Produces(MediaType.APPLICATION_JSON)
public Response getRatings(@QueryParam("userId") int userId) {
    try {
        // Input validation
        if (userId <= 0) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Invalid user ID");
        }

        JSONArray ratings = ratingService.getUserRatings(userId);
        
        // Return empty array instead of error if no ratings
        return Response.ok(ratings.toString()).build();
        
    } catch (IllegalArgumentException e) {
        return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
    } catch (Exception e) {
        logger.error("API request failed", e);
        return buildErrorResponse(
            Response.Status.INTERNAL_SERVER_ERROR,
            "Failed to retrieve ratings"
        );
    }
}

private Response buildErrorResponse(Response.Status status, String message) {
    return Response.status(status)
        .entity("{\"error\":\"" + message + "\"}")
        .build();
}

@GET
    @Path("/movie/{movieId}/average")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAverageRatingForMovie(@PathParam("movieId") int movieId) {
        try {
            JSONObject response = ratingService.getAverageRatingForMovie(movieId);
            return Response.ok(response.toString()).build();
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Failed to calculate average rating");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(error.toString())
                         .build();
        }
    }

    @GET
@Path("/by-rounded-rating/{rating}")
@Produces(MediaType.APPLICATION_JSON)
public Response getMoviesByRoundedRating(
        @PathParam("rating") int rating,
        @QueryParam("minVotes") @DefaultValue("1") int minVotes,
        @QueryParam("minYear") @DefaultValue("1900") int minYear,
        @QueryParam("maxYear") @DefaultValue("2100") int maxYear,
        @QueryParam("minLength") @DefaultValue("0") int minLength,
        @QueryParam("maxLength") @DefaultValue("999") int maxLength) {
    
    try {
        // Validate parameters
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (minYear > maxYear) {
            throw new IllegalArgumentException("minYear cannot be greater than maxYear");
        }
        if (minLength > maxLength) {
            throw new IllegalArgumentException("minLength cannot be greater than maxLength");
        }
        
        JSONArray movies = ratingService.getMoviesByRoundedRating(rating);
        
        // Filter based on parameters
        JSONArray filteredMovies = new JSONArray();
        for (int i = 0; i < movies.length(); i++) {
            JSONObject movie = movies.getJSONObject(i);
            int ratingCount = movie.getInt("ratingCount");
            int releaseYear = movie.has("releaseYear") ? movie.getInt("releaseYear") : 0;
            int length = movie.has("length") ? movie.getInt("length") : 0;
            
            if (ratingCount >= minVotes && 
                releaseYear >= minYear && 
                releaseYear <= maxYear &&
                length >= minLength && 
                length <= maxLength) {
                filteredMovies.put(movie);
            }
        }
        
        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("roundedRating", rating);
        response.put("count", filteredMovies.length());
        response.put("movies", filteredMovies);
        
        return Response.ok(response.toString()).build();
    } catch (IllegalArgumentException e) {
        JSONObject error = new JSONObject();
        error.put("status", "error");
        error.put("message", e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                     .entity(error.toString())
                     .build();
    } catch (Exception e) {
        JSONObject error = new JSONObject();
        error.put("status", "error");
        error.put("message", "Failed to fetch movies");
        error.put("details", e.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                     .entity(error.toString())
                     .build();
    }
}
}