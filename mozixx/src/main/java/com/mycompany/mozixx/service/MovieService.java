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
import javax.persistence.Query;
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
            // NATIVE QUERY használata közvetlen SQL-lel
            Query query = em.createNativeQuery(
                "SELECT movie_id, title, cover FROM movies ORDER BY RAND() LIMIT ?")
                .setParameter(1, count);
            
            List<Object[]> results = query.getResultList();
            
            for (Object[] row : results) {
                JSONObject movie = new JSONObject();
                movie.put("movieId", row[0]);
                movie.put("title", row[1]);
                movie.put("cover", row[2] != null ? row[2] : JSONObject.NULL);
                movies.put(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Hiba a filmek lekérdezésekor", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return movies;
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
    
    public List<Movies> getLatestReleases() {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("GetLatestReleases");
            List<Object[]> resultList = query.getResultList();
            List<Movies> movies = new ArrayList<>();
            
            for (Object[] row : resultList) {
                Movies movie = new Movies();
                movie.setMovieId(parseInt(row[0]));
                movie.setMovieName(toString(row[1]));
                movie.setLength(parseInt(row[2]));
                movie.setCover(toString(row[3]));
                movie.setTrailerLink(toString(row[4]));
                movies.add(movie);
            }
            return movies;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    private Integer parseInt(Object value) {
        if (value == null) return null;
        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null; // vagy dobj egy kivételt, ha szükséges
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }
    
    // Egyéb metódusok...
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}