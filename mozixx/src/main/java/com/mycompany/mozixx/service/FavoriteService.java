/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.service;

import com.mycompany.mozixx.config.JWT;
import static com.mysql.cj.conf.PropertyKey.logger;
import io.jsonwebtoken.Claims;
import java.util.Date;
import org.slf4j.LoggerFactory;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
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
        // 1. JWT token validálása
        int validationResult = JWT.validateJWT(jwtToken);
        if (validationResult != 1) {
            response.put("status", "error");
            response.put("statusCode", 401);
            response.put("message", validationResult == 3 ? "Lejárt token" : "Érvénytelen token");
            return response;
        }

        // 2. Felhasználó ID kinyerése
        Integer userId = JWT.getUserIdByToken(jwtToken);
        if (userId == null) {
            response.put("status", "error");
            response.put("statusCode", 401);
            response.put("message", "Érvénytelen felhasználói azonosító a tokenben");
            return response;
        }

        transaction = em.getTransaction();
        transaction.begin();

        // 3. Tárolt eljárás hívása
        StoredProcedureQuery query = em.createStoredProcedureQuery("AddMovieToFavorites")
            .registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN)
            .registerStoredProcedureParameter("p_movie_id", Integer.class, ParameterMode.IN)
            .setParameter("p_user_id", userId)
            .setParameter("p_movie_id", movieId);

        query.execute();
        transaction.commit();

        response.put("status", "success");
        response.put("statusCode", 200);
        response.put("message", "Film sikeresen hozzáadva a kedvencekhez");
        
    } catch (Exception e) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
        
        response.put("status", "error");
        response.put("statusCode", 500);
        response.put("message", "Szerverhiba: " + e.getMessage());
    }
    
    return response;
}


   public JSONArray getUserFavorites(String jwt) {
    JSONArray favorites = new JSONArray();
    EntityManager em = emf.createEntityManager();
    
    try {
        // Get user ID from JWT
        int userId = JWT.getUserIdByToken(jwt);
        
        // Call stored procedure
        StoredProcedureQuery query = em.createStoredProcedureQuery("GetUserFavorites")
            .registerStoredProcedureParameter("userId", Integer.class, ParameterMode.IN)
            .setParameter("userId", userId);
        
        // Execute and process results
        List<Object[]> results = query.getResultList();
        
        for (Object[] row : results) {
            JSONObject movie = new JSONObject();
            movie.put("movieId", row[0]);
            movie.put("title", row[1] != null ? row[1] : "");
            movie.put("releaseYear", row[2] != null ? row[2] : "");
            movie.put("cover", row[3] != null ? row[3] : JSONObject.NULL);
            favorites.put(movie);
        }
    } catch (Exception e) {
        logger.error("Error fetching favorites", e);
        throw new RuntimeException("Failed to retrieve favorites");
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
    return favorites;
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