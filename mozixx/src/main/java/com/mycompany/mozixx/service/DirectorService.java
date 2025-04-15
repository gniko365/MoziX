package com.mycompany.mozixx.service;

import com.mycompany.mozixx.model.Directors;
import com.mycompany.mozixx.model.MovieDirectors;
import com.mycompany.mozixx.model.MovieDirectorsPK;
import com.mycompany.mozixx.model.Movies;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;

public class DirectorService implements AutoCloseable {

    private final EntityManagerFactory emf;
    private final EntityManager em;

    public DirectorService() {
        this.emf = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");
        this.em = emf.createEntityManager();
    }

    // Összes rendező lekérése
    public List<Directors> getAllDirectors() {
        return em.createQuery("SELECT d FROM Directors d", Directors.class).getResultList();
    }

    // Rendező lekérése ID alapján
    public Directors getDirectorById(int directorId) {
        return em.find(Directors.class, directorId);
    }

    // Rendező létrehozása
    public void createDirector(String name, String directorImage, String birthDate) {
        em.getTransaction().begin();
        try {
            Directors director = new Directors();
            director.setName(name);
            director.setDirectorImage(directorImage);
            director.setBirthDate(java.sql.Date.valueOf(birthDate));
            em.persist(director);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    // Rendező frissítése
    public void updateDirector(int directorId, String name, String directorImage, String birthDate) {
        em.getTransaction().begin();
        try {
            Directors director = em.find(Directors.class, directorId);
            if (director != null) {
                director.setName(name);
                director.setDirectorImage(directorImage);
                director.setBirthDate(java.sql.Date.valueOf(birthDate));
                em.merge(director);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    // Rendező törlése
    public void deleteDirector(int directorId) {
        em.getTransaction().begin();
        try {
            Directors director = em.find(Directors.class, directorId);
            if (director != null) {
                em.remove(director);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    // Film hozzáadása egy rendezőhöz
    public void addMovieToDirector(int directorId, int movieId) {
        em.getTransaction().begin();
        try {
            Directors director = em.find(Directors.class, directorId);
            Movies movie = em.find(Movies.class, movieId);
            if (director != null && movie != null) {
                MovieDirectors movieDirectors = new MovieDirectors(movieId, directorId);
                movieDirectors.setDirectors(director);
                movieDirectors.setMovies(movie);
                em.persist(movieDirectors);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    // Film eltávolítása egy rendezőtől
    public void removeMovieFromDirector(int directorId, int movieId) {
        em.getTransaction().begin();
        try {
            MovieDirectorsPK pk = new MovieDirectorsPK(movieId, directorId);
            MovieDirectors movieDirectors = em.find(MovieDirectors.class, pk);
            if (movieDirectors != null) {
                em.remove(movieDirectors);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    // Rendezők lekérése film ID alapján (tárolt eljárással)
    public List<Directors> getDirectorsByMovieId(int movieId) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("GetDirectorsByMovie");
        query.registerStoredProcedureParameter("p_movie_id", Integer.class, ParameterMode.IN)
             .setParameter("p_movie_id", movieId);
        
        return query.getResultList();
    }

    // Rendezők lekérése film ID alapján (JPQL-lel)
    public List<Directors> getDirectorsByMovieIdJpql(int movieId) {
        return em.createQuery(
            "SELECT d FROM Directors d JOIN d.movies m WHERE m.movieId = :movieId", 
            Directors.class)
            .setParameter("movieId", movieId)
            .getResultList();
    }

    // Erőforrások lezárása
    @Override
    public void close() {
        try {
            if (em != null && em.isOpen()) {
                em.close();
            }
        } finally {
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }
}