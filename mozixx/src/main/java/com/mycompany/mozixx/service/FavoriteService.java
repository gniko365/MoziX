/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.service;

import static com.mysql.cj.conf.PropertyKey.logger;
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

    public FavoriteService() {
        this.emf = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");
    }

    public JSONObject addFavorite(int userId, int movieId) {
    JSONObject response = new JSONObject();
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = null;
    
    try {
        tx = em.getTransaction();
        tx.begin();

        // 1. Ellenőrizzük, hogy létezik-e a film
        boolean movieExists = ((Number) em.createNativeQuery(
            "SELECT COUNT(*) FROM movies WHERE movie_id = ?")
            .setParameter(1, movieId)
            .getSingleResult()).intValue() > 0;
        
        if (!movieExists) {
            response.put("status", "error");
            response.put("message", "Movie not found");
            return response;
        }

        // 2. Ellenőrizzük, hogy létezik-e már a kedvenc
        boolean alreadyFavorite = ((Number) em.createNativeQuery(
            "SELECT COUNT(*) FROM user_favorites WHERE user_id = ? AND movie_id = ?")
            .setParameter(1, userId)
            .setParameter(2, movieId)
            .getSingleResult()).intValue() > 0;
        
        if (alreadyFavorite) {
            response.put("status", "error");
            response.put("message", "Movie already in favorites");
            return response;
        }

        // 3. Új kedvenc hozzáadása
        em.createNativeQuery(
            "INSERT INTO user_favorites (user_id, movie_id) VALUES (?, ?)")
            .setParameter(1, userId)
            .setParameter(2, movieId)
            .executeUpdate();

        tx.commit();
        
        response.put("status", "success");
        response.put("message", "Movie added to favorites");
    } catch (Exception e) {
        if (tx != null && tx.isActive()) tx.rollback();
        response.put("status", "error");
        response.put("message", "Error: " + e.getMessage());
        logger.error("Error adding favorite", e);
    } finally {
        em.close();
    }
    return response;
}


   public JSONArray getUserFavorites(int userId) {
    JSONArray favorites = new JSONArray();
    EntityManager em = emf.createEntityManager();
    
    try {
        // Use exact property names from Movies entity
        List<Object[]> results = em.createQuery(
            "SELECT m.movieId, m.movieName, m.releaseYear, m.cover " +
            "FROM UserFavorites uf JOIN uf.movieId m " +
            "WHERE uf.userId.userId = :userId " +
            "ORDER BY uf.addedAt DESC", Object[].class)
            .setParameter("userId", userId)
            .getResultList();

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
   
   public JSONObject deleteFavorite(int userId, int movieId) {
    JSONObject response = new JSONObject();
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = null;
    
    try {
        tx = em.getTransaction();
        tx.begin();

        // Tárolt eljárás meghívása
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