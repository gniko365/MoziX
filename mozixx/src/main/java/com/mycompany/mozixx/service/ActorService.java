package com.mycompany.mozixx.service;

import com.mycompany.mozixx.model.Actors;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;

public class ActorService {

    private EntityManagerFactory emf;
    private EntityManager em;

    public ActorService() {
        emf = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");
        em = emf.createEntityManager();
    }

    public List<Actors> getAllActors() {
        StoredProcedureQuery query = em.createStoredProcedureQuery("GetAllActors", Actors.class);
        return query.getResultList();
    }

    public List<Actors> getActorsByMovie(String movieTitle) {
    StoredProcedureQuery query = em.createStoredProcedureQuery("GetActorsByMovie", "ActorMapping");
    query.registerStoredProcedureParameter("movie_title", String.class, ParameterMode.IN);
    query.setParameter("movie_title", movieTitle);
    return query.getResultList();
}

    public List<Object[]> getMoviesByActor(String actorName) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("getMoviesByActor");
        query.registerStoredProcedureParameter("actor_name", String.class, ParameterMode.IN);
        query.setParameter("actor_name", actorName);
        return query.getResultList();
    }

    public List<Object[]> getAllActorsInMovies() {
        StoredProcedureQuery query = em.createStoredProcedureQuery("everyActorInAMovie");
        return query.getResultList();
    }

    public void close() {
        if (em != null) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }
}