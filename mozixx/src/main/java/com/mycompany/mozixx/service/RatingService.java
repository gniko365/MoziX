package com.mycompany.mozixx.service;

import com.mycompany.mozixx.model.Ratings;
import static com.mysql.cj.conf.PropertyKey.logger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.activation.DataSource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

public class RatingService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FavoriteService.class);
    private EntityManagerFactory emf;
    private EntityManager em;
    private DataSource dataSource;  // Injektáld vagy inicializáld a dataSource-t

    public RatingService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public RatingService() {
        emf = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");
        em = emf.createEntityManager();
    }

    public void addRating(int userId, int movieId, int rating, String review) {
    try {
        System.out.println("Starting transaction for user ID: " + userId + ", movie ID: " + movieId); // Naplózás
        em.getTransaction().begin();

        StoredProcedureQuery query = em.createStoredProcedureQuery("AddRating")
            .registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN)
            .registerStoredProcedureParameter("p_movie_id", Integer.class, ParameterMode.IN)
            .registerStoredProcedureParameter("p_rating", Integer.class, ParameterMode.IN)
            .registerStoredProcedureParameter("p_review", String.class, ParameterMode.IN)
            .setParameter("p_user_id", userId)
            .setParameter("p_movie_id", movieId)
            .setParameter("p_rating", rating)
            .setParameter("p_review", review);

        System.out.println("Executing stored procedure AddRating..."); // Naplózás
        query.execute();
        em.getTransaction().commit();
        System.out.println("Transaction committed successfully."); // Naplózás
    } catch (Exception e) {
        System.err.println("Error adding rating: " + e.getMessage()); // Naplózás
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
            System.err.println("Transaction rolled back due to error."); // Naplózás
        }
        throw new RuntimeException("Failed to add rating: " + e.getMessage(), e);
    } finally {
        em.close();
        emf.close();
        System.out.println("EntityManager and EntityManagerFactory closed."); // Naplózás
    }
}

    public void deleteRatingById(int ratingId, int userId) {
    try {
        System.out.println("Starting transaction for rating ID: " + ratingId + ", user ID: " + userId); // Naplózás
        em.getTransaction().begin();

        // Tárolt eljárás meghívása
        StoredProcedureQuery query = em.createStoredProcedureQuery("DeleteRatingById")
            .registerStoredProcedureParameter("p_rating_id", Integer.class, ParameterMode.IN)
            .registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN)
            .setParameter("p_rating_id", ratingId)
            .setParameter("p_user_id", userId);

        System.out.println("Executing stored procedure DeleteRatingById..."); // Naplózás
        query.execute();
        em.getTransaction().commit();
        System.out.println("Transaction committed successfully."); // Naplózás
    } catch (Exception e) {
        System.err.println("Error deleting rating: " + e.getMessage()); // Naplózás
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
            System.err.println("Transaction rolled back due to error."); // Naplózás
        }
        throw new RuntimeException("Failed to delete rating: " + e.getMessage(), e);
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        System.out.println("EntityManager and EntityManagerFactory closed."); // Naplózás
    }
}
    public JSONArray getUserRatings(int userId) {
    JSONArray ratings = new JSONArray();
    EntityManager em = emf.createEntityManager();
    
    try {
        // Tárolt eljárás meghívása
        StoredProcedureQuery query = em.createStoredProcedureQuery("GetUserRatings")
            .registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN)
            .setParameter("p_user_id", userId);

        // Eredmények lekérése
        List<Object[]> results = query.getResultList();

        // Eredmény feldolgozása
        for (Object[] row : results) {
            JSONObject ratingObj = new JSONObject();
            
            // Rating adatok
            ratingObj.put("ratingId", row[0]);
            ratingObj.put("ratingValue", row[1]);
            ratingObj.put("review", row[2] != null ? row[2] : JSONObject.NULL);
            ratingObj.put("ratingDate", row[3] != null ? row[3].toString() : JSONObject.NULL);
            
            // Movie adatok
            JSONObject movieObj = new JSONObject();
            movieObj.put("movieId", row[4]);
            movieObj.put("title", row[5] != null ? row[5] : "");
            movieObj.put("releaseYear", row[6] != null ? row[6] : "");
            movieObj.put("cover", row[7] != null ? row[7] : JSONObject.NULL);
            
            ratingObj.put("movie", movieObj);
            ratings.put(ratingObj);
        }
    } catch (Exception e) {
        logger.error("Error fetching ratings with stored procedure", e);
        throw new RuntimeException("Failed to retrieve ratings using stored procedure");
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
    return ratings;
}
    
    public JSONObject getAverageRatingForMovie(int movieId) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("CalculateAverageRatingForMovie")
                .registerStoredProcedureParameter("p_movie_id", Integer.class, ParameterMode.IN)
                .setParameter("p_movie_id", movieId);
            
            Object[] result = (Object[]) query.getSingleResult();
            
            JSONObject response = new JSONObject();
            response.put("movieId", result[0]);
            response.put("title", result[1]);
            response.put("averageRating", result[2]);
            response.put("ratingCount", result[3]);
            
            return response;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // Filmek lekérdezése adott átlagértékeléssel
    public JSONArray getMoviesByAverageRating(double ratingValue) {
        JSONArray movies = new JSONArray();
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery("GetMoviesByAverageRating")
                .registerStoredProcedureParameter("p_rating_value", Double.class, ParameterMode.IN)
                .setParameter("p_rating_value", ratingValue);
            
            List<Object[]> results = query.getResultList();
            
            for (Object[] row : results) {
                JSONObject movie = new JSONObject();
                movie.put("movieId", row[0]);
                movie.put("title", row[1]);
                movie.put("cover", row[2] != null ? row[2] : JSONObject.NULL);
                movie.put("averageRating", row[3]);
                movie.put("ratingCount", row[4]);
                movies.put(movie);
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return movies;
    }
    public JSONArray getMoviesByRoundedRating(int roundedRating) {
    JSONArray movies = new JSONArray();
    EntityManager em = emf.createEntityManager();
    try {
        StoredProcedureQuery query = em.createStoredProcedureQuery("GetMoviesByRoundedRating")
            .registerStoredProcedureParameter("p_rounded_rating", Integer.class, ParameterMode.IN)
            .setParameter("p_rounded_rating", roundedRating);
        
        List<Object[]> results = query.getResultList();
        
        for (Object[] row : results) {
            JSONObject movie = new JSONObject();
            movie.put("movieId", row[0]);
            movie.put("title", row[1]);
            movie.put("cover", row[2] != null ? row[2] : JSONObject.NULL);
            movie.put("exactAverage", row[3]);
            movie.put("ratingCount", row[4]);
            movie.put("roundedRating", roundedRating);
            movies.put(movie);
        }
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
    return movies;
}
}