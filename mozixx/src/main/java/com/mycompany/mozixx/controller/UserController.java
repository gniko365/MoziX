package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.config.JWT;
import com.mycompany.mozixx.model.Users;
import com.mycompany.mozixx.service.UserService;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("users")
public class UserController {

    @Context
    private UriInfo context;
    private UserService layer = new UserService();

    public UserController() {
        this.userService = new UserService();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        throw new UnsupportedOperationException();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
    
    private UserService userService;
    
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(String bodyString) {
        JSONObject body = new JSONObject(bodyString);

        JSONObject obj = layer.login(body.getString("email"), body.getString("password"));
        return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @POST
@Path("registerUser")
@Consumes(MediaType.APPLICATION_JSON)
public Response registerUser(String bodyString) {
    JSONObject body = new JSONObject(bodyString);

    // Felhasználó létrehozása a kérésből
    Users user = new Users(
            body.getString("email"),
            body.getString("username"),
            body.getString("password")
    );

    // Regisztráció meghívása a Service rétegben
    JSONObject obj = layer.registerUser(user);

    // Válasz visszaadása
    return Response.status(obj.getInt("statusCode"))
                  .entity(obj.toString())
                  .type(MediaType.APPLICATION_JSON)
                  .build();
}

    @POST
    @Path("registerAdmin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAdmin(@HeaderParam("token") String jwt, String bodyString) {
        JSONObject body = new JSONObject(bodyString);

        Users u = new Users(
                body.getString("email"),
                body.getString("username"),
                body.getString("password")
        );

        JSONObject obj = layer.registerAdmin(u, jwt);
        return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("getAllUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllUser(@HeaderParam("token") String jwt) {
        int isValid = JWT.validateJWT(jwt);

        if (isValid == 1) {
            JSONObject obj = layer.getAllUser();
            return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
        } else if (isValid == 2) {
            return Response.status(498).entity("InvalidToken").type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(401).entity("TokenExpired").type(MediaType.APPLICATION_JSON).build();
        }
    }

    @GET
    @Path("getUserById")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserById(@HeaderParam("token") String jwt, @QueryParam("userId") Integer userId) {
        int isValid = JWT.validateJWT(jwt);

        if (isValid == 1) {
            JSONObject obj = layer.getUserById(userId);
            return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
        } else if (isValid == 2) {
            return Response.status(498).entity("InvalidToken").type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(401).entity("TokenExpired").type(MediaType.APPLICATION_JSON).build();
        }
    }
    
    @DELETE
    @Path("/delete/{userId}")
    public Response deleteUser(@PathParam("userId") Integer userId) {
        try {
            userService.beginTransaction();
            Users user = userService.getEntityManager().find(Users.class, userId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\": \"User not found\"}")
                               .build();
            }
            userService.getEntityManager().remove(user);
            userService.commitTransaction();
            return Response.ok(new JSONObject().put("message", "User deleted successfully").toString()).build();
        } catch (Exception e) {
            userService.rollbackTransaction();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } finally {
            userService.close();
        }
    }
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        try {
            List<Users> users = userService.getAllUsers();
            JSONArray jsonArray = new JSONArray(users);
            return Response.ok(jsonArray.toString()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } finally {
            userService.close();
        }
    }
    
    @PUT
    @Path("/update/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("userId") Integer userId, String jsonBody) {
        try {
            JSONObject body = new JSONObject(jsonBody);
            userService.beginTransaction();
            Users user = userService.getEntityManager().find(Users.class, userId);
            
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\": \"User not found\"}")
                               .build();
            }
            
            if (body.has("username")) user.setUsername(body.getString("username"));
            if (body.has("email")) user.setEmail(body.getString("email"));
            if (body.has("password")) user.setPassword(body.getString("password"));
            if (body.has("role")) user.setRole(body.getString("role"));
            
            userService.getEntityManager().merge(user);
            userService.commitTransaction();
            
            return Response.ok(new JSONObject().put("message", "User updated successfully").toString()).build();
        } catch (Exception e) {
            userService.rollbackTransaction();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        } finally {
            userService.close();
        }
    }
}