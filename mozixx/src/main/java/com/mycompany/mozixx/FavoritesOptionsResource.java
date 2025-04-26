package com.mycompany.mozixx; // Cseréld le a saját csomagnevedre

import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/favorites/{movieId: \\d+}")
public class FavoritesOptionsResource {

    @OPTIONS
    public Response handleOptions() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with")
                .header("Access-Control-Allow-Credentials", "true")
                .build();
    }
}