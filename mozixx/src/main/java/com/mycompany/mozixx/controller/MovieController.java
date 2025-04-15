/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.service.MovieService;
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