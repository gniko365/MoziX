package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.model.Directors;
import com.mycompany.mozixx.model.MovieDirectors;
import com.mycompany.mozixx.model.Movies;
import com.mycompany.mozixx.service.DirectorService;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/directors")
public class DirectorController {

    private DirectorService directorService = new DirectorService();

    // Összes rendező lekérése
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDirectors() {
        try {
            List<Directors> directors = directorService.getAllDirectors();
            JSONArray jsonArray = new JSONArray(directors);
            return Response.ok(jsonArray.toString()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } finally {
            directorService.close();
        }
    }

    // Rendező lekérése ID alapján
    @GET
    @Path("/by-id/{directorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDirectorById(@PathParam("directorId") int directorId) {
        try {
            Directors director = directorService.getDirectorById(directorId);
            if (director != null) {
                JSONObject jsonObject = new JSONObject(director);
                return Response.ok(jsonObject.toString()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\": \"Director not found\"}")
                               .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } finally {
            directorService.close();
        }
    }

    // Rendező létrehozása
    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDirector(String jsonInput) {
        try {
            JSONObject jsonObject = new JSONObject(jsonInput);
            String name = jsonObject.getString("name");
            String directorImage = jsonObject.getString("directorImage");
            String birthDate = jsonObject.getString("birthDate");

            directorService.createDirector(name, directorImage, birthDate);
            return Response.status(Response.Status.CREATED)
                           .entity("{\"message\": \"Director created successfully\"}")
                           .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } finally {
            directorService.close();
        }
    }

    // Rendező frissítése
    @PUT
    @Path("/update/{directorId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDirector(@PathParam("directorId") int directorId, String jsonInput) {
        try {
            JSONObject jsonObject = new JSONObject(jsonInput);
            String name = jsonObject.getString("name");
            String directorImage = jsonObject.getString("directorImage");
            String birthDate = jsonObject.getString("birthDate");

            directorService.updateDirector(directorId, name, directorImage, birthDate);
            return Response.ok("{\"message\": \"Director updated successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } finally {
            directorService.close();
        }
    }

    // Rendező törlése
    @DELETE
    @Path("/delete/{directorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDirector(@PathParam("directorId") int directorId) {
        try {
            directorService.deleteDirector(directorId);
            return Response.ok("{\"message\": \"Director deleted successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } finally {
            directorService.close();
        }
    }
    
    @GET
@Path("/{movieId}/directors")
@Produces(MediaType.APPLICATION_JSON)
public Response getMovieDirectors(@PathParam("movieId") int movieId) {
    try {
        List directors = directorService.getDirectorsByMovieId(movieId);
        return Response.ok(directors).build();
    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                     .entity("{\"error\":\"Failed to get directors: " + e.getMessage() + "\"}")
                     .build();
    }
}
}