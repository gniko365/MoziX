/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.controller;

import com.mycompany.mozixx.config.JWT; // Szükséges a JWT ellenőrzéshez
import com.mycompany.mozixx.model.Users; // Szükséges a User.Role enumhoz, ha a JWT-ből szerepköröt olvasunk
import com.mycompany.mozixx.service.MovieService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.DELETE; // DELETE metódushoz
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.HeaderParam; // Header paraméterhez
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
    public Response getMoviesWithDetails() {
        try {
            JSONArray movies = movieService.getMoviesWithDetails();
            
            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("count", movies.length());
            response.put("movies", movies);
            
            return Response.ok(response.toString()).build();
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Failed to fetch movies");
            error.put("details", e.getMessage());
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(error.toString())
                           .build();
        }
    }
    @GET
    @Path("/random")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRandomMovies() {
        try {
            JSONArray randomMovies = movieService.getRandomMovies(4);
            
            // Átlag értékelés kiszámolása az összes visszaadott filmre
            double overallAverage = calculateOverallAverage(randomMovies);
            
            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("count", randomMovies.length());
            response.put("overallAverageRating", overallAverage);
            response.put("movies", randomMovies);
            
            return Response.ok(response.toString()).build();
            
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Nem sikerült lekérni a filmeket");
            error.put("details", e.getMessage());
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error.toString())
                .build();
        }
    }

    private double calculateOverallAverage(JSONArray movies) {
        if (movies.length() == 0) return 0.0;
        
        double sum = 0;
        int count = 0;
        
        for (int i = 0; i < movies.length(); i++) {
            JSONObject movie = movies.getJSONObject(i);
            // Ellenőrizzük, hogy az "averageRating" létezik és nem null
            if (movie.has("averageRating") && !movie.isNull("averageRating")) {
                double rating = movie.getDouble("averageRating");
                if (rating > 0) {
                    sum += rating;
                    count++;
                }
            }
        }
        
        return count > 0 ? Math.round((sum / count) * 10) / 10.0 : 0.0;
    }
    
    @GET
    @Path("/latest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatestReleases() {
        try {
            List<Map<String, Object>> movies = movieService.getLatestReleases();
            // A List<Map<String, Object>> típus közvetlen JSON-né alakítása
            JSONArray jsonArray = new JSONArray(movies);
            return Response.ok(jsonArray.toString()).build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(new JSONObject(error).toString()) // Map-ből JSONObject-et készítünk
                           .build();
        }
    }
    
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMoviesByName(@QueryParam("q") String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", "Keresési kifejezés megadása kötelező");
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity(error.toString())
                               .build();
            }

            JSONArray movies = movieService.searchMoviesByName(searchTerm);

            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("searchTerm", searchTerm);
            response.put("count", movies.length());
            response.put("movies", movies);

            return Response.ok(response.toString()).build();

        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Hiba történt a filmek keresése közben");
            error.put("details", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(error.toString())
                           .build();
        }
    }
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieById(@PathParam("id") int movieId) {
        try {
            JSONObject movie = movieService.getMovieById(movieId);
            
            if (movie == null) {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", "Film nem található");
                return Response.status(Response.Status.NOT_FOUND)
                               .entity(error.toString())
                               .build();
            }
            
            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("movie", movie);
            
            return Response.ok(response.toString()).build();
            
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", "Hiba történt a film lekérdezése közben");
            error.put("details", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(error.toString())
                           .build();
        }
    }

    /**
     * Töröl egy filmet a rendszerből. Csak 'admin' szerepkörrel rendelkező felhasználók számára elérhető.
     * A JWT token a "token" fejlécben érkezik, és tartalmaznia kell a felhasználó szerepkörét.
     *
     * @param jwt A felhasználó JWT tokenje.
     * @param movieId A törlendő film azonosítója.
     * @return HTTP válasz a művelet sikerességéről vagy hibájáról.
     */
    @DELETE
    @Path("/{movieId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovie(@HeaderParam("token") String jwt, @PathParam("movieId") int movieId) {
        // 1. JWT érvényességének ellenőrzése
        int isValid = JWT.validateJWT(jwt);
        if (isValid == 2) { // Invalid token
            return Response.status(498) // Custom status code for InvalidToken
                           .entity(new JSONObject().put("status", "error").put("message", "Invalid Token").toString())
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        } else if (isValid == 0) { // Token expired
            return Response.status(401) // Unauthorized
                           .entity(new JSONObject().put("status", "error").put("message", "Token Expired").toString())
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }

        // 2. Szerepkör ellenőrzése
        // Feltételezzük, hogy a JWT osztályban van egy statikus metódus, ami visszaadja a szerepkört a tokenből.
        // Ha nincs, akkor ezt implementálni kell a JWT.java fájlban.
        // Példa: public static String getRoleFromToken(String jwtToken) { ... }
        String userRole = JWT.getUserRoleByToken(jwt); // Helyes metódusnév használata

        if (!"admin".equalsIgnoreCase(userRole)) {
            return Response.status(Response.Status.FORBIDDEN) // 403 Forbidden
                           .entity(new JSONObject().put("status", "error").put("message", "Nincs jogosultságod ehhez a művelethez. Admin jogosultság szükséges.").toString())
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }

        // 3. Film törlése a service rétegen keresztül
        JSONObject result = movieService.deleteMovie(movieId);

        // 4. Válasz összeállítása a service eredménye alapján
        return Response.status(result.getInt("statusCode"))
                       .entity(result.toString())
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
