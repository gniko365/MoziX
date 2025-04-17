/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.model.Movies;
import com.mycompany.mozixx.service.MovieService;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/movies")
public class MovieController {
    private MovieService movieService = new MovieService();
    
    @GET
@Path("/all")
@Produces(MediaType.APPLICATION_JSON)
public Response GetMovies() {
    try {
        List<Movies> movies = movieService.GetMovies();
        JSONArray jsonArray = new JSONArray(movies);  // Direct konverzió List -> JSON
        return Response.ok(jsonArray.toString()).build();
    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                      .entity("{\"error\": \"" + e.getMessage() + "\"}")
                      .build();
    } finally {
        movieService.close();  // Ha close() metódusod van
    }
}
    @GET
    @Path("/random")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRandomMovies() {
        try {
            // 4 véletlenszerű film lekérése
            JSONArray randomMovies = movieService.getRandomMovies(4);
            
            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("data", randomMovies);
            
            return Response.ok(response.toString()).build();
        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to fetch random movies");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(errorResponse.toString())
                         .build();
        }
    }
}