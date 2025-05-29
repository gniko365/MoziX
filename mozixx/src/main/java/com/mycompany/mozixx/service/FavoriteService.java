package com.mycompany.mozixx.service;

import com.mycompany.mozixx.config.JWT;
import com.mycompany.mozixx.model.Movies;
import static com.mysql.cj.conf.PropertyKey.logger;
import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import jdk.jpackage.internal.Log.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class FavoriteService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FavoriteService.class);
    private final EntityManagerFactory emf;
    private final EntityManager em;


    public FavoriteService() {
        this.emf = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");
        this.em = emf.createEntityManager();
    }

    public JSONObject addMovieToFavorites(String jwtToken, int movieId) {
    JSONObject response = new JSONObject();
    EntityTransaction transaction = null;
    
    try {
        // 1. JWT token validation
        int validationResult = JWT.validateJWT(jwtToken);
        if (validationResult != 1) {
            response.put("status", "error");
            response.put("statusCode", 401);
            response.put("message", validationResult == 3 ? "Lejárt token" : "Érvénytelen token");
            return response;
        }

        // 2. Get user ID
        Integer userId = JWT.getUserIdByToken(jwtToken);
        if (userId == null) {
            response.put("status", "error");
            response.put("statusCode", 401);
            response.put("message", "Érvénytelen felhasználói azonosító a tokenben");
            return response;
        }

        transaction = em.getTransaction();
        transaction.begin();

        // 3. Call stored procedure with native query to avoid result set expectation
        try {
            Query query = em.createNativeQuery(
                "CALL AddMovieToFavorites(?, ?)")
                .setParameter(1, userId)
                .setParameter(2, movieId);
            
            query.executeUpdate();
            transaction.commit();
            
            response.put("status", "success");
            response.put("statusCode", 200);
            response.put("message", "Film sikeresen hozzáadva a kedvencekhez");
            
        } catch (PersistenceException e) {
            // Handle specific database errors
            Throwable rootCause = getRootCause(e);
            String errorMsg = rootCause.getMessage();
            
            if (errorMsg.contains("foreign key constraint")) {
                response.put("message", "Érvénytelen felhasználó vagy film azonosító");
            } else if (errorMsg.contains("duplicate entry")) {
                response.put("message", "A film már szerepel a kedvencek között");
            } else {
                response.put("message", "Adatbázis hiba: " + errorMsg);
            }
            
            response.put("status", "error");
            response.put("statusCode", 400); // Bad request
            transaction.rollback();
        }
        
    } catch (Exception e) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
        response.put("status", "error");
        response.put("statusCode", 500);
        response.put("message", "Váratlan hiba: " + e.getMessage());
    }
    
    return response;
}

private Throwable getRootCause(Throwable throwable) {
    Throwable rootCause = throwable;
    while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
        rootCause = rootCause.getCause();
    }
    return rootCause;
}


   private List<Map<String, Object>> parsePeopleInfo(String info, String type) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (info != null && !info.trim().isEmpty()) {
            String[] people = info.split("\\|");
            for (String person : people) {
                String[] parts = person.split(":", 3); // Korlátozzuk 3 részre a URL miatt
                if (parts.length == 3) {
                    Map<String, Object> personMap = new HashMap<>();
                    try {
                        personMap.put("id", Integer.parseInt(parts[0].trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("Hiba a " + type + " ID parszolásakor: " + parts[0]);
                        personMap.put("id", null); // Vagy valamilyen alapértelmezett érték
                    }
                    personMap.put("name", parts[1].trim());
                    personMap.put("image", parts[2].trim().isEmpty() ? null : parts[2].trim());
                    personMap.put("type", type);
                    result.add(personMap);
                } else {
                    System.err.println("Rossz formátumú " + type + " adat: " + person);
                }
            }
        }
        return result;
    }

    private List<Map<String, Object>> parseGenresInfo(String info) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (info != null && !info.trim().isEmpty()) {
            String[] genres = info.split("\\|");
            for (String genre : genres) {
                String[] parts = genre.split(":");
                if (parts.length >= 2) {
                    Map<String, Object> genreMap = new HashMap<>();
                    try {
                        genreMap.put("id", Integer.parseInt(parts[0].trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("Hiba a műfaj ID parszolásakor: " + parts[0]);
                        genreMap.put("id", null); // Vagy valamilyen alapértelmezett érték
                    }
                    genreMap.put("name", parts[1].trim());
                    result.add(genreMap);
                } else {
                    System.err.println("Rossz formátumú műfaj adat: " + genre);
                }
            }
        }
        return result;
    }

    public JSONObject getFavoriteMovies(String jwtToken) {
        JSONObject response = new JSONObject();

        
        Integer userId = null;
        int validationResult = JWT.validateJWT(jwtToken);
        if (validationResult == 1) {
            userId = JWT.getUserIdByToken(jwtToken);
            System.out.println("Kinyert userId: " + userId); // Logoljuk ki a userId-t
        } else {
            response.put("status", "error");
            response.put("statusCode", 401);
            response.put("message", validationResult == 3 ? "Lejárt token" : "Érvénytelen token");
            return response;
        }

        if (userId == null) {
            response.put("status", "error");
            response.put("statusCode", 401);
            response.put("message", "Érvénytelen felhasználói azonosító a tokenben");
            return response;
        }

        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("GetUserFavorites");
            query.registerStoredProcedureParameter("userId", Integer.class, ParameterMode.IN);
            query.setParameter("userId", userId);

            List<Object[]> results = query.getResultList();
            JSONArray favoriteMovies = new JSONArray();

            for (Object[] result : results) {
                JSONObject movieData = new JSONObject();
                movieData.put("movieId", result[0]);
                movieData.put("title", result[1]);
                movieData.put("cover", result[2]);
                movieData.put("releaseYear", result[3]);
                movieData.put("length", result[4]);
                movieData.put("description", result[5]);
                movieData.put("trailerLink", result[6]);
                movieData.put("averageRating", result[7]);
                movieData.put("directors", new JSONArray(parsePeopleInfo((String) result[8], "director")));
                movieData.put("actors", new JSONArray(parsePeopleInfo((String) result[9], "actor")));
                movieData.put("genres", new JSONArray(parseGenresInfo((String) result[10])));

                favoriteMovies.put(movieData);
            }

            response.put("status", "success");
            response.put("statusCode", 200);
            response.put("data", favoriteMovies);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("statusCode", 500);
            response.put("message", "Szerverhiba a kedvenc filmek lekérdezésekor: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
   
   public JSONObject deleteFavorite(String jwt, int movieId) {
    JSONObject response = new JSONObject();
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = null;
    
    try {
        // Get user ID from JWT
        int userId = JWT.getUserIdByToken(jwt);
        
        tx = em.getTransaction();
        tx.begin();

        // Call stored procedure
        StoredProcedureQuery query = em.createStoredProcedureQuery("DeleteUserFavorite")
            .registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN)
            .registerStoredProcedureParameter("p_movie_id", Integer.class, ParameterMode.IN)
            .registerStoredProcedureParameter("p_result", Integer.class, ParameterMode.OUT)
            .setParameter("p_user_id", userId)
            .setParameter("p_movie_id", movieId);

        query.execute();
        
        int result = (Integer) query.getOutputParameterValue("p_result");
        
        tx.commit();
        
        if (result == 1) {
            response.put("status", "success");
            response.put("message", "Favorite removed successfully");
        } else {
            response.put("status", "error");
            response.put("message", "Favorite not found");
        }
    } catch (Exception e) {
        if (tx != null && tx.isActive()) tx.rollback();
        response.put("status", "error");
        response.put("message", "Error: " + e.getMessage());
        logger.error("Error deleting favorite", e);
    } finally {
        em.close();
    }
    return response;
}
}