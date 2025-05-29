package com.mycompany.mozixx.service;

import com.mycompany.mozixx.model.Ratings;
import static com.mysql.cj.conf.PropertyKey.logger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private DataSource dataSource;

    public RatingService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public RatingService() {
        emf = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");
        em = emf.createEntityManager();
    }

    public void addRating(int userId, int movieId, int rating, String review) {
    try {
        System.out.println("Starting transaction for user ID: " + userId + ", movie ID: " + movieId);
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

        System.out.println("Executing stored procedure AddRating...");
        query.execute();
        em.getTransaction().commit();
        System.out.println("Transaction committed successfully.");
    } catch (Exception e) {
        System.err.println("Error adding rating: " + e.getMessage());
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
            System.err.println("Transaction rolled back due to error.");
        }
        throw new RuntimeException("Failed to add rating: " + e.getMessage(), e);
    } finally {
        em.close();
        emf.close();
        System.out.println("EntityManager and EntityManagerFactory closed.");
    }
}

    public void deleteRatingById(int ratingId, int userId) {
    try {
        System.out.println("Starting transaction for rating ID: " + ratingId + ", user ID: " + userId);
        em.getTransaction().begin();

        StoredProcedureQuery query = em.createStoredProcedureQuery("DeleteRatingById")
            .registerStoredProcedureParameter("p_rating_id", Integer.class, ParameterMode.IN)
            .registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN)
            .setParameter("p_rating_id", ratingId)
            .setParameter("p_user_id", userId);

        System.out.println("Executing stored procedure DeleteRatingById...");
        query.execute();
        em.getTransaction().commit();
        System.out.println("Transaction committed successfully.");
    } catch (Exception e) {
        System.err.println("Error deleting rating: " + e.getMessage());
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
            System.err.println("Transaction rolled back due to error.");
        }
        throw new RuntimeException("Failed to delete rating: " + e.getMessage(), e);
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        System.out.println("EntityManager and EntityManagerFactory closed.");
    }
}
    public JSONArray getUserRatings(int userId) {
    JSONArray ratings = new JSONArray();
    EntityManager em = emf.createEntityManager();
    
    try {
        StoredProcedureQuery query = em.createStoredProcedureQuery("GetUserRatings")
            .registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN)
            .setParameter("p_user_id", userId);

        List<Object[]> results = query.getResultList();

        for (Object[] row : results) {
            JSONObject ratingObj = new JSONObject();
            
            ratingObj.put("ratingId", row[0]);
            ratingObj.put("ratingValue", row[1]);
            ratingObj.put("review", row[2] != null ? row[2] : JSONObject.NULL);
            ratingObj.put("ratingDate", row[3] != null ? row[3].toString() : JSONObject.NULL);
            
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
            movie.put("cover", row[2]);
            movie.put("description", row[3] != null ? row[3] : JSONObject.NULL);
            movie.put("length", row[4] != null ? row[4] : JSONObject.NULL);
            movie.put("releaseYear", row[5] != null ? row[5] : JSONObject.NULL);
            movie.put("exactAverage", row[6]);
            movie.put("ratingCount", row[7]);
            movie.put("trailerLink", row[8] != null ? row[8] : JSONObject.NULL);
            movie.put("roundedRating", roundedRating);
            
            JSONArray directorsArray = new JSONArray();
            String directorsInfo = row[9] != null ? row[9].toString() : "";
            for (Map<String, String> director : parsePeopleInfo(directorsInfo, "director")) {
                JSONObject directorJson = new JSONObject();
                directorJson.put("id", director.get("id"));
                directorJson.put("name", director.get("name"));
                directorJson.put("image", director.get("image"));
                directorsArray.put(directorJson);
            }
            movie.put("directors", directorsArray);
            
            JSONArray actorsArray = new JSONArray();
            String actorsInfo = row[10] != null ? row[10].toString() : "";
            for (Map<String, String> actor : parsePeopleInfo(actorsInfo, "actor")) {
                JSONObject actorJson = new JSONObject();
                actorJson.put("id", actor.get("id"));
                actorJson.put("name", actor.get("name"));
                actorJson.put("image", actor.get("image"));
                actorsArray.put(actorJson);
            }
            movie.put("actors", actorsArray);
            
            JSONArray genresArray = new JSONArray();
            String genresInfo = row[11] != null ? row[11].toString() : "";
            for (Map<String, String> genre : parseGenresInfo(genresInfo)) {
                JSONObject genreJson = new JSONObject();
                genreJson.put("id", genre.get("id"));
                genreJson.put("name", genre.get("name"));
                genresArray.put(genreJson);
            }
            movie.put("genres", genresArray);
            
            movies.put(movie);
        }
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
    return movies;
}

private List<Map<String, String>> parsePeopleInfo(String info, String type) {
    List<Map<String, String>> result = new ArrayList<>();
    if (info != null && !info.trim().isEmpty()) {
        String[] people = info.split("\\|");
        for (String person : people) {
            String[] parts = person.split(":", 3);
            if (parts.length == 3) {
                Map<String, String> personMap = new HashMap<>();
                personMap.put("id", parts[0]);
                personMap.put("name", parts[1]);
                personMap.put("image", parts[2].isEmpty() ? null : parts[2]);
                personMap.put("type", type);
                result.add(personMap);
            }
        }
    }
    return result;
}

private List<Map<String, String>> parseGenresInfo(String info) {
    List<Map<String, String>> result = new ArrayList<>();
    if (info != null && !info.trim().isEmpty()) {
        String[] genres = info.split("\\|");
        for (String genre : genres) {
            String[] parts = genre.split(":");
            if (parts.length >= 2) {
                Map<String, String> genreMap = new HashMap<>();
                genreMap.put("id", parts[0]);
                genreMap.put("name", parts[1]);
                result.add(genreMap);
            }
        }
    }
    return result;
}
}