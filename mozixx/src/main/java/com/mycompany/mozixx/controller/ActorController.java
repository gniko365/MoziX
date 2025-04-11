package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.model.Actors;
import com.mycompany.mozixx.service.ActorService;
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

@Path("/actors")
public class ActorController {

    private ActorService actorService = new ActorService();

    // CREATE
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createActor(Actors actor) {
        try {
            actorService.beginTransaction();
            actorService.getEntityManager().persist(actor);
            actorService.commitTransaction();
            return Response.status(Response.Status.CREATED)
                           .entity(new JSONObject().put("message", "Actor created successfully").toString())
                           .build();
        } catch (Exception e) {
            actorService.rollbackTransaction();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } finally {
            actorService.close();
        }
    }

    // READ (existing methods)
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
    @Path("/by-id/{actorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActorById(@PathParam("actorId") Integer actorId) {
        try {
            Actors actor = actorService.getEntityManager().find(Actors.class, actorId);
            if (actor == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\": \"Actor not found\"}")
                               .build();
            }
            JSONObject jsonObject = new JSONObject(actor);
            return Response.ok(jsonObject.toString()).build();
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

    // UPDATE
    @PUT
    @Path("/update/{actorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateActor(@PathParam("actorId") Integer actorId, Actors updatedActor) {
        try {
            actorService.beginTransaction();
            Actors existingActor = actorService.getEntityManager().find(Actors.class, actorId);
            if (existingActor == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\": \"Actor not found\"}")
                               .build();
            }
            
            // Update the fields
            if (updatedActor.getName() != null) {
                existingActor.setName(updatedActor.getName());
            }
            if (updatedActor.getBirthDate() != null) {
                existingActor.setBirthDate(updatedActor.getBirthDate());
            }
            if (updatedActor.getActorImage() != null) {
                existingActor.setActorImage(updatedActor.getActorImage());
            }
            
            actorService.getEntityManager().merge(existingActor);
            actorService.commitTransaction();
            
            return Response.ok(new JSONObject().put("message", "Actor updated successfully").toString()).build();
        } catch (Exception e) {
            actorService.rollbackTransaction();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } finally {
            actorService.close();
        }
    }

    // DELETE
    @DELETE
    @Path("/delete/{actorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteActor(@PathParam("actorId") Integer actorId) {
        try {
            actorService.beginTransaction();
            Actors actor = actorService.getEntityManager().find(Actors.class, actorId);
            if (actor == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\": \"Actor not found\"}")
                               .build();
            }
            actorService.getEntityManager().remove(actor);
            actorService.commitTransaction();
            return Response.ok(new JSONObject().put("message", "Actor deleted successfully").toString()).build();
        } catch (Exception e) {
            actorService.rollbackTransaction();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } finally {
            actorService.close();
        }
    }
}