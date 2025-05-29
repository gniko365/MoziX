package com.mycompany.mozixx.service;

import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import org.json.JSONArray;
import org.json.JSONObject;


public class GenreService {
    private static final EntityManagerFactory emf = 
        Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");

    public JSONArray getMoviesByGenreIdWithDetails(int genreId) {
    EntityManager em = null;
    JSONArray jsonArray = new JSONArray();
    
    try {
        em = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT").createEntityManager();
        StoredProcedureQuery spq = em.createStoredProcedureQuery("GetMoviesByGenreIdWithDetails")
            .registerStoredProcedureParameter("p_genre_id", Integer.class, ParameterMode.IN)
            .setParameter("p_genre_id", genreId);
        
        spq.execute();
        List<Object[]> results = spq.getResultList();
        
        for (Object[] row : results) {
            JSONObject jsonMovie = new JSONObject();
            jsonMovie.put("movieId", row[0]);
            jsonMovie.put("title", row[1]);
            jsonMovie.put("cover", row[2]);
            jsonMovie.put("releaseYear", row[3]);
            jsonMovie.put("length", row[4]);
            jsonMovie.put("description", row[5]);
            jsonMovie.put("trailerLink", row[6]);
            jsonMovie.put("averageRating", row[7]);
            
            // Process directors
            JSONArray directorsArray = new JSONArray();
            String directorsInfo = row[8] != null ? row[8].toString() : "";
            for (Map<String, String> director : parsePeopleInfo(directorsInfo, "director")) {
                JSONObject directorJson = new JSONObject();
                directorJson.put("id", director.get("id"));
                directorJson.put("name", director.get("name"));
                directorJson.put("image", director.get("image"));
                directorsArray.put(directorJson);
            }
            jsonMovie.put("directors", directorsArray);
            
            // Process actors
            JSONArray actorsArray = new JSONArray();
            String actorsInfo = row[9] != null ? row[9].toString() : "";
            for (Map<String, String> actor : parsePeopleInfo(actorsInfo, "actor")) {
                JSONObject actorJson = new JSONObject();
                actorJson.put("id", actor.get("id"));
                actorJson.put("name", actor.get("name"));
                actorJson.put("image", actor.get("image"));
                actorsArray.put(actorJson);
            }
            jsonMovie.put("actors", actorsArray);
            
            // Process genres
            JSONArray genresArray = new JSONArray();
            String genresInfo = row[10] != null ? row[10].toString() : "";
            for (Map<String, String> genre : parseGenresInfo(genresInfo)) {
                JSONObject genreJson = new JSONObject();
                genreJson.put("id", genre.get("id"));
                genreJson.put("name", genre.get("name"));
                genresArray.put(genreJson);
            }
            jsonMovie.put("genres", genresArray);
            
            jsonArray.put(jsonMovie);
        }
    } catch (Exception e) {
        throw new RuntimeException("Error fetching movies by genre", e);
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
    
    return jsonArray;
}

private static List<Map<String, String>> parsePeopleInfo(String info, String type) {
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

private static List<Map<String, String>> parseGenresInfo(String info) {
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
