/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.service;

import com.mycompany.mozixx.model.Movies;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author szter
 */
public class MovieService {
    // Statikus EntityManagerFactory inicializálása
    private Movies layer = new Movies();
    private static final EntityManagerFactory emf = 
        Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");
    @PersistenceContext
    private EntityManager em;
    
    public JSONArray getRandomMovies(int count) {
        JSONArray movies = new JSONArray();
        EntityManager em = emf.createEntityManager();
        
        try {
            // Tárolt eljárás meghívása
            StoredProcedureQuery query = em.createStoredProcedureQuery("GetRandomMovies")
                .registerStoredProcedureParameter("p_count", Integer.class, ParameterMode.IN)
                .setParameter("p_count", count);
            
            // Eredmények lekérése
            List<Object[]> results = query.getResultList();
            
            // Eredmény feldolgozása
            for (Object[] row : results) {
                JSONObject movie = new JSONObject();
                movie.put("movieId", row[0]);
                movie.put("title", row[1]);
                movie.put("cover", row[2] != null ? row[2] : JSONObject.NULL);
                movies.put(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch random movies", e);
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
    
    public ArrayList<Movies> GetMovies() {
    ArrayList<Movies> movieList = new ArrayList<>(); 
    try {
        movieList = layer.getMovies();

    } catch (Exception e) {
        System.err.println("Error fetching shoes: " + e.getMessage());
    }

    return movieList;
}
}