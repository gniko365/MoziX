package com.mycompany.mozixx.service;

import com.mycompany.mozixx.model.Movies;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    public JSONArray getMoviesWithDetails() {
    ArrayList<Map<String, Object>> movieList = new ArrayList<>(); 
    JSONArray jsonArray = new JSONArray();
    
    try {
        movieList = Movies.getMoviesWithDetails();
        
        for (Map<String, Object> movie : movieList) {
            JSONObject jsonMovie = new JSONObject();
            // Alap adatok
            jsonMovie.put("movieId", movie.get("movieId"));
            jsonMovie.put("title", movie.get("title"));
            jsonMovie.put("cover", movie.get("cover"));
            jsonMovie.put("releaseYear", movie.get("releaseYear"));
            jsonMovie.put("length", movie.get("length"));
            jsonMovie.put("description", movie.get("description"));
            jsonMovie.put("trailerLink", movie.get("trailerLink"));
            jsonMovie.put("averageRating", movie.get("averageRating"));
            
            // Rendezők JSON tömbbe
            JSONArray directorsArray = new JSONArray();
            for (Map<String, String> director : (List<Map<String, String>>) movie.get("directors")) {
                JSONObject directorJson = new JSONObject();
                directorJson.put("id", director.get("id"));
                directorJson.put("name", director.get("name"));
                directorJson.put("image", director.get("image"));
                directorsArray.put(directorJson);
            }
            jsonMovie.put("directors", directorsArray);
            
            // Színészek JSON tömbbe
            JSONArray actorsArray = new JSONArray();
            for (Map<String, String> actor : (List<Map<String, String>>) movie.get("actors")) {
                JSONObject actorJson = new JSONObject();
                actorJson.put("id", actor.get("id"));
                actorJson.put("name", actor.get("name"));
                actorJson.put("image", actor.get("image"));
                actorsArray.put(actorJson);
            }
            jsonMovie.put("actors", actorsArray);
            
            // Műfajok JSON tömbbe
            JSONArray genresArray = new JSONArray();
            for (Map<String, String> genre : (List<Map<String, String>>) movie.get("genres")) {
                JSONObject genreJson = new JSONObject();
                genreJson.put("id", genre.get("id"));
                genreJson.put("name", genre.get("name"));
                genresArray.put(genreJson);
            }
            jsonMovie.put("genres", genresArray);
            
            jsonArray.put(jsonMovie);
        }
    } catch (Exception e) {
        System.err.println("Error fetching movies: " + e.getMessage());
        e.printStackTrace();
    }
    
    return jsonArray;
}
    
    public List<Map<String, Object>> getLatestReleases() {
    EntityManager em = getEntityManagerFactory().createEntityManager();
    try {
        StoredProcedureQuery query = em.createStoredProcedureQuery("GetLatestReleases");
        query.execute();
        List<Object[]> resultList = query.getResultList();
        List<Map<String, Object>> movies = new ArrayList<>();
        
        for (Object[] row : resultList) {
            Map<String, Object> movie = new HashMap<>();
            movie.put("movieId", parseInt(row[0]));
            movie.put("title", toString(row[1]));
            movie.put("cover", toString(row[2]));
            movie.put("releaseYear", parseInt(row[3]));
            movie.put("length", parseInt(row[4]));
            movie.put("description", toString(row[5]));
            movie.put("trailerLink", toString(row[6]));
            movie.put("averageRating", row[7] != null ? ((Number)row[7]).doubleValue() : null);
            
            // Rendezők feldolgozása
            movie.put("directors", parsePeopleInfo(toString(row[8]), "director"));
            
            // Színészek feldolgozása
            movie.put("actors", parsePeopleInfo(toString(row[9]), "actor"));
            
            // Műfajok feldolgozása
            movie.put("genres", parseGenresInfo(toString(row[10])));
            
            movies.add(movie);
        }
        return movies;
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}

private List<Map<String, Object>> parsePeopleInfo(String info, String type) {
    List<Map<String, Object>> result = new ArrayList<>();
    if (info != null && !info.trim().isEmpty()) {
        String[] people = info.split("\\|");
        for (String person : people) {
            String[] parts = person.split(":", 3); // Korlátozzuk 3 részre a URL miatt
            if (parts.length == 3) {
                Map<String, Object> personMap = new HashMap<>();
                personMap.put("id", parseInt(parts[0]));
                personMap.put("name", parts[1]);
                personMap.put("image", parts[2].isEmpty() ? null : parts[2]);
                personMap.put("type", type);
                result.add(personMap);
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
                genreMap.put("id", parseInt(parts[0]));
                genreMap.put("name", parts[1]);
                result.add(genreMap);
            }
        }
    }
    return result;
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
                int movieId = (int) row[0];
                movie.put("movieId", movieId);
                movie.put("title", row[1]);
                movie.put("cover", row[2] != null ? row[2] : JSONObject.NULL);
                movie.put("releaseYear", row[3]);
                movie.put("length", row[4] != null ? row[4] : JSONObject.NULL);
                movie.put("description", row[5] != null ? row[5] : JSONObject.NULL);
                movie.put("trailerLink", row[6] != null ? row[6] : JSONObject.NULL);
                movie.put("averageRating", row[7]);

                movie.put("directors", getDirectorsByMovieId(em, movieId));
                movie.put("actors", getActorsByMovieId(em, movieId));
                movie.put("genres", getGenresByMovieId(em, movieId));

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

    private JSONArray getDirectorsByMovieId(EntityManager em, int movieId) {
        List<Object[]> directorsResult = em.createNativeQuery("SELECT d.director_id AS id, d.name, d.director_image FROM movie_directors md JOIN directors d ON md.director_id = d.director_id WHERE md.movie_id = :movieId")
                .setParameter("movieId", movieId)
                .getResultList();
        JSONArray directors = new JSONArray();
        for (Object[] row : directorsResult) {
            JSONObject director = new JSONObject();
            director.put("id", row[0]);
            director.put("name", row[1]);
            director.put("image", row[2] != null ? row[2] : JSONObject.NULL); // Itt 'image' marad a JSON kulcs
            directors.put(director);
        }
        return directors;
    }

    private JSONArray getActorsByMovieId(EntityManager em, int movieId) {
        List<Object[]> actorsResult = em.createNativeQuery("SELECT a.actor_id AS id, a.name, a.actor_image FROM movie_actors ma JOIN actors a ON ma.actor_id = a.actor_id WHERE ma.movie_id = :movieId")
                .setParameter("movieId", movieId)
                .getResultList();
        JSONArray actors = new JSONArray();
        for (Object[] row : actorsResult) {
            JSONObject actor = new JSONObject();
            actor.put("id", row[0]);
            actor.put("name", row[1]);
            actor.put("image", row[2] != null ? row[2] : JSONObject.NULL); // Itt 'image' marad a JSON kulcs
            actors.put(actor);
        }
        return actors;
    }

    private JSONArray getGenresByMovieId(EntityManager em, int movieId) {
        List<Object[]> genresResult = em.createNativeQuery("SELECT g.genre_id AS id, g.name FROM movie_genres mg JOIN genres g ON mg.genre_id = g.genre_id WHERE mg.movie_id = :movieId")
                .setParameter("movieId", movieId)
                .getResultList();
        JSONArray genres = new JSONArray();
        for (Object[] row : genresResult) {
            JSONObject genre = new JSONObject();
            genre.put("id", row[0]);
            genre.put("name", row[1]);
            genres.put(genre);
        }
        return genres;
    }
    public JSONObject getMovieById(int movieId) {
    EntityManager em = getEntityManagerFactory().createEntityManager();
    try {
        StoredProcedureQuery query = em.createStoredProcedureQuery("GetMovieById")
            .registerStoredProcedureParameter("p_movie_id", Integer.class, ParameterMode.IN)
            .setParameter("p_movie_id", movieId);
        
        List<Object[]> results = query.getResultList();
        
        if (results.isEmpty()) {
            return null;
        }
        
        Object[] row = results.get(0);
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
        
        return movie;
    } catch (Exception e) {
        throw new RuntimeException("Hiba a film lekérdezésekor ID alapján: " + movieId, e);
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}
}