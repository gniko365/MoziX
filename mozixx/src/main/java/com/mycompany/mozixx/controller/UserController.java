package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.config.JWT;
import com.mycompany.mozixx.model.Users;
import com.mycompany.mozixx.service.UserService;
import java.util.List;
import javax.persistence.EntityTransaction;
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
import org.json.JSONException;
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
    
    public enum Role {
    user, admin;
    
    public static Role fromString(String value) {
        try {
            return Role.valueOf(value.toLowerCase());
        } catch (IllegalArgumentException e) {
            return user; // default to user if invalid
        }
    }
}
    
    private UserService userService;
    
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(String bodyString) {
        JSONObject body = new JSONObject(bodyString);

        JSONObject obj = layer.login(body.getString("username"), body.getString("password"));
        return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @POST
@Path("/register")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response registerUser(String jsonRequest) {
    try {
        JSONObject request = new JSONObject(jsonRequest);
        
        // Validációk
        if (!request.has("email") || request.getString("email").isEmpty() ||
            !request.has("username") || request.getString("username").isEmpty() ||
            !request.has("password") || request.getString("password").isEmpty()) {
            
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse(400, "Minden mező kitöltése kötelező").toString())
                .build();
        }

        Users user = new Users(
            request.getString("email"),
            request.getString("username"),
            request.getString("password")
        );

        JSONObject result = userService.registerUser(user);
        
        // Explicit toString() hívás és Content-Type beállítás
        return Response.status(result.getInt("statusCode"))
            .entity(result.toString())
            .type(MediaType.APPLICATION_JSON)
            .build();
            
    } catch (JSONException e) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(createErrorResponse(400, "Érvénytelen JSON formátum").toString())
            .type(MediaType.APPLICATION_JSON)
            .build();
    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(createErrorResponse(500, "Szerverhiba: " + e.getMessage()).toString())
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}

private JSONObject createErrorResponse(int statusCode, String message) {
    return new JSONObject()
        .put("status", "error")
        .put("statusCode", statusCode)
        .put("message", message);
}

        @POST
@Path("registerAdmin")
@Consumes(MediaType.APPLICATION_JSON)
public Response registerAdmin(String bodyString) {
    try {
        JSONObject body = new JSONObject(bodyString);

        Users u = new Users(
                body.getString("email"),
                body.getString("username"),
                body.getString("password")
        );
        JSONObject obj = userService.registerAdmin(u); // JWT nélkül hívjuk meg a service-t
        return Response.status(obj.getInt("statusCode")).entity(obj.toString()).type(MediaType.APPLICATION_JSON).build();
    } catch (JSONException e) {
        return Response.status(400)
                .entity(createErrorResponse(400, "Érvénytelen JSON formátum").toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
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
    
    // UserController.java

@DELETE
@Path("/delete/{userId}")
@Produces(MediaType.APPLICATION_JSON)
public Response deleteUser(@PathParam("userId") Integer userId, @HeaderParam("X-Password") String password) {
    if (password == null || password.isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse(400, "A jelszó megadása kötelező az X-Password header-ben").toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    JSONObject result = userService.deleteUser(userId, password);
    return Response.status(result.getInt("statusCode"))
            .entity(result.toString())
            .type(MediaType.APPLICATION_JSON)
            .build();
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
        if (body.has("role")) {
            user.setRole(Users.Role.fromString(body.getString("role")));
        }
        
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

@POST
@Path("/update-username")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response updateUsername(String requestBody) {
    try {
        JSONObject request = new JSONObject(requestBody);
        
        // Kötelező mezők ellenőrzése
        if (!request.has("userId") || !request.has("currentPassword") || !request.has("newUsername")) {
            return Response.status(400)
                         .entity("{\"error\":\"Missing required fields\"}")
                         .build();
        }

        JSONObject result = userService.updateUsername(
            request.getInt("userId"),
            request.getString("currentPassword"),
            request.getString("newUsername")
        );

        return Response.status(result.getString("status").equals("success") ? 200 : 400)
                     .entity(result.toString())
                     .build();

    } catch (Exception e) {
        return Response.status(500)
                     .entity("{\"error\":\"" + e.getMessage() + "\"}")
                     .build();
    }
}

}