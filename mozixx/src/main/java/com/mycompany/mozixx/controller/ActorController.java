package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.model.Actors;
import com.mycompany.mozixx.service.ActorService;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/actors")
public class ActorController {

    private ActorService actorService = new ActorService();

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllActors() {
        try {
            List<Actors> actors = actorService.getAllActors();
            JSONArray jsonArray = new JSONArray(actors);
            return Response.ok(jsonArray.toString()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } finally {
            actorService.close();
        }
    }

    @GET
@Path("/by-movie/{movieTitle}")
@Produces(MediaType.APPLICATION_JSON)
public Response getActorsByMovie(@PathParam("movieTitle") String movieTitle) {
    try {
        List<Actors> actors = actorService.getActorsByMovie(movieTitle);
        JSONArray jsonArray = new JSONArray(actors);
        return Response.ok(jsonArray.toString()).build();
    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity("{\"error\": \"" + e.getMessage() + "\"}")
                       .build();
    } finally {
        actorService.close();
    }
}

    @GET
@Path("/movies-by-actor/{actorName}")
@Produces(MediaType.APPLICATION_JSON)
public Response getMoviesByActor(@PathParam("actorName") String actorName) {
    try {
        List<Object[]> movies = actorService.getMoviesByActor(actorName);
        JSONArray jsonArray = new JSONArray();
        for (Object[] movie : movies) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("movieName", movie[0]);
            jsonObject.put("releaseYear", movie[1]);
            jsonArray.put(jsonObject);
        }
        return Response.ok(jsonArray.toString()).build();
    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity("{\"error\": \"" + e.getMessage() + "\"}")
                       .build();
    } finally {
        actorService.close();
    }
}

    @GET
@Path("/all-in-movies")
@Produces(MediaType.APPLICATION_JSON)
public Response getAllActorsInMovies() {
    try {
        List<Object[]> results = actorService.getAllActorsInMovies();
        JSONArray jsonArray = new JSONArray();
        for (Object[] result : results) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("movieId", result[0]);
            jsonObject.put("actorCount", result[1]);
            jsonArray.put(jsonObject);
        }
        return Response.ok(jsonArray.toString()).build();
    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity("{\"error\": \"" + e.getMessage() + "\"}")
                       .build();
    } finally {
        actorService.close();
    }
}
}