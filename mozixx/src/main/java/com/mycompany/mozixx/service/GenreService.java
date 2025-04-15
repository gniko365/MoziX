/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author szter
 */
public class GenreService {
    private static final EntityManagerFactory emf = 
        Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");

    public JSONArray getMoviesByGenreId(int genreId) {
        JSONArray movies = new JSONArray();
        EntityManager em = emf.createEntityManager();
        
        try {
            // Tárolt eljárás meghívása
            StoredProcedureQuery query = em.createStoredProcedureQuery("GetMoviesByGenreId")
                .registerStoredProcedureParameter("p_genre_id", Integer.class, ParameterMode.IN)
                .setParameter("p_genre_id", genreId);
            
            // Eredmények lekérése
            List<Object[]> results = query.getResultList();
            
            // Eredmény feldolgozása
            for (Object[] row : results) {
                JSONObject movie = new JSONObject();
                movie.put("movieId", row[0]);
                movie.put("title", row[1]);
                movie.put("cover", row[2] != null ? row[2] : JSONObject.NULL);
                movie.put("releaseYear", row[3]);
                movie.put("genre", row[4]); // Műfaj neve is visszaadásra kerül
                movies.put(movie);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch movies by genre ID", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        
        return movies;
    }
    
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
