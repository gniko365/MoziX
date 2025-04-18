/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.model.Movies;
import com.mycompany.mozixx.service.MovieService;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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
        List<Movies> movies = movieService.getMovies();
        JSONArray jsonArray = new JSONArray(movies);  // Direct konverzió List -> JSON
        return Response.ok(jsonArray.toString()).build();
    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                      .entity("{\"error\": \"" + e.getMessage() + "\"}")
                      .build();
    }
}
    @GET
    @Path("/random")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRandomMovies() {
        try {
            JSONArray randomMovies = movieService.getRandomMovies(4);
            
            // Sikeres válasz
            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("count", randomMovies.length());
            response.put("data", randomMovies);
            
            return Response.ok(response.toString()).build();
            
        } catch (Exception e) {
            // Hiba esetén
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Nem sikerült lekérni a filmeket");
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error.toString())
                .build();
        }
    }
    
    @GET
    @Path("/latest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatestReleases() {
        try {
            List<Movies> movies = movieService.getLatestReleases();
            return Response.ok(movies).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity("{\"error\":\"" + e.getMessage() + "\"}")
                         .build();
        }
    }
}