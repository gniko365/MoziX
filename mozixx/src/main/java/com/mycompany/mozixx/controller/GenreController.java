/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.service.GenreService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/genres")
public class GenreController {
    private GenreService genreService = new GenreService();
    
    @GET
    @Path("/{genreId}/movies")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMoviesByGenreId(@PathParam("genreId") int genreId) {
        try {
            JSONArray movies = genreService.getMoviesByGenreId(genreId);
            
            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("genreId", genreId);
            response.put("count", movies.length());
            response.put("movies", movies);
            
            return Response.ok(response.toString()).build();
        } catch (Exception e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to fetch movies by genre ID");
            errorResponse.put("details", e.getMessage());
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(errorResponse.toString())
                         .build();
        }
    }
}