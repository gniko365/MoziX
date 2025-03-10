package com.mycompany.mozixx.service;

import com.mycompany.mozixx.model.Directors;
import com.mycompany.mozixx.model.MovieDirectors;
import com.mycompany.mozixx.model.MovieDirectorsPK;
import com.mycompany.mozixx.model.Movies;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DirectorService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");
    private EntityManager em = emf.createEntityManager();

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
        Directors director = new Directors();
        director.setName(name);
        director.setDirectorImage(directorImage);
        director.setBirthDate(java.sql.Date.valueOf(birthDate));

        em.getTransaction().begin();
        em.persist(director);
        em.getTransaction().commit();
    }

    // Rendező frissítése
    public void updateDirector(int directorId, String name, String directorImage, String birthDate) {
        Directors director = em.find(Directors.class, directorId);
        if (director != null) {
            director.setName(name);
            director.setDirectorImage(directorImage);
            director.setBirthDate(java.sql.Date.valueOf(birthDate));

            em.getTransaction().begin();
            em.merge(director);
            em.getTransaction().commit();
        }
    }

    // Rendező törlése
    public void deleteDirector(int directorId) {
        Directors director = em.find(Directors.class, directorId);
        if (director != null) {
            em.getTransaction().begin();
            em.remove(director);
            em.getTransaction().commit();
        }
    }

    // Film hozzáadása egy rendezőhöz
    public void addMovieToDirector(int directorId, int movieId) {
        Directors director = em.find(Directors.class, directorId);
        Movies movie = em.find(Movies.class, movieId);
        if (director != null && movie != null) {
            MovieDirectors movieDirectors = new MovieDirectors(movieId, directorId);
            movieDirectors.setDirectors(director);
            movieDirectors.setMovies(movie);

            em.getTransaction().begin();
            em.persist(movieDirectors);
            em.getTransaction().commit();
        }
    }

    // Film eltávolítása egy rendezőtől
    public void removeMovieFromDirector(int directorId, int movieId) {
        MovieDirectorsPK pk = new MovieDirectorsPK(movieId, directorId);
        MovieDirectors movieDirectors = em.find(MovieDirectors.class, pk);
        if (movieDirectors != null) {
            em.getTransaction().begin();
            em.remove(movieDirectors);
            em.getTransaction().commit();
        }
    }

    // EntityManager lezárása
    public void close() {
        if (em != null) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }
}