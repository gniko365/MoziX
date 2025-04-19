package com.mycompany.mozixx.service;

import com.mycompany.mozixx.model.Movies;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class MovieService {
    private Movies layer = new Movies();
    private static EntityManagerFactory emf;
    private static final Object EMF_LOCK = new Object();
    
    private static EntityManagerFactory getEntityManagerFactory() {
        synchronized (EMF_LOCK) {
            if (emf == null || !emf.isOpen()) {
                emf = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (emf != null && emf.isOpen()) {
                            emf.close();
                        }
                    }
                }));
            }
            return emf;
        }
    }
    
    public JSONArray getRandomMovies(int count) {
    EntityManager em = getEntityManagerFactory().createEntityManager();
    try {
        StoredProcedureQuery query = em.createStoredProcedureQuery("GetRandomMovies");
        
        List<Object[]> results = query.getResultList();
        JSONArray movies = new JSONArray();
        
        for (Object[] row : results) {
            JSONObject movie = new JSONObject();
            movie.put("movieId", row[0]);
            movie.put("title", row[1]);
            movie.put("cover", row[2] != null ? row[2] : JSONObject.NULL);
            movie.put("averageRating", row[3]);
            movies.put(movie);
        }
        return movies;
    } catch (Exception e) {
        throw new RuntimeException("Hiba a filmek lekérdezésekor", e);
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}
    
    public ArrayList<Movies> getMovies() {
        ArrayList<Movies> movieList = new ArrayList<>(); 
        try {
            movieList = new Movies().getMovies();
        } catch (Exception e) {
            System.err.println("Error fetching movies: " + e.getMessage());
        }
        return movieList;
    }
    
    public List<Movies> getLatestReleases() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
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
            return null;
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }
    
    public JSONArray searchMoviesByName(String searchTerm) {
    EntityManager em = getEntityManagerFactory().createEntityManager();
    try {
        StoredProcedureQuery query = em.createStoredProcedureQuery("SearchMoviesByName")
            .registerStoredProcedureParameter("p_search_term", String.class, ParameterMode.IN)
            .setParameter("p_search_term", searchTerm);
        
        List<Object[]> results = query.getResultList();
        JSONArray movies = new JSONArray();
        
        for (Object[] row : results) {
            JSONObject movie = new JSONObject();
            movie.put("movieId", row[0]);
            movie.put("title", row[1]);
            movie.put("cover", row[2] != null ? row[2] : JSONObject.NULL);
            movie.put("releaseYear", row[3]);
            movie.put("length", row[4] != null ? row[4] : JSONObject.NULL);
            movie.put("description", row[5] != null ? row[5] : JSONObject.NULL);
            movie.put("trailerLink", row[6] != null ? row[6] : JSONObject.NULL);
            movie.put("averageRating", row[7]);
            movie.put("genres", row[8] != null ? row[8] : "");
            movies.put(movie);
        }
        return movies;
    } catch (Exception e) {
        throw new RuntimeException("Hiba a filmek keresésekor: " + searchTerm, e);
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}
}