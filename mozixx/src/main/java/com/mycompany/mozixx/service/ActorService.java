package com.mycompany.mozixx.service;

import com.mycompany.mozixx.model.Actors;
import java.sql.Date;
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

    public List<Actors> getActorsByMovieId(int movieId) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("GetActorsByMovie");
        query.registerStoredProcedureParameter("p_movie_id", Integer.class, ParameterMode.IN)
             .setParameter("p_movie_id", movieId);
        
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
    public EntityManager getEntityManager() {
        return em;
    }

    public void beginTransaction() {
        em.getTransaction().begin();
    }

    public void commitTransaction() {
        em.getTransaction().commit();
    }

    public void rollbackTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
    
    public void createActor(String name, String actorImage, String birthDate) {
        Actors actor = new Actors();
        actor.setName(name);
        actor.setActorImage(actorImage);
        actor.setBirthDate(java.sql.Date.valueOf(birthDate));

        em.getTransaction().begin();
        em.persist(actor);
        em.getTransaction().commit();
    }

    // Színész frissítése
    public void updateActor(int actorId, String name, String actorImage, String birthDate) {
        Actors actor = em.find(Actors.class, actorId);
        if (actor != null) {
            actor.setName(name);
            actor.setActorImage(actorImage);
            actor.setBirthDate(java.sql.Date.valueOf(birthDate));

            em.getTransaction().begin();
            em.merge(actor);
            em.getTransaction().commit();
        }
    }

    public String deleteActor(Integer actorId) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("DeleteActor");
        query.registerStoredProcedureParameter("p_actor_id", Integer.class, ParameterMode.IN);
        query.setParameter("p_actor_id", actorId);
        query.execute();
        return (String) query.getSingleResult();
    }

    public Actors getActorById(Integer actorId) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("GetActorById", Actors.class);
        query.registerStoredProcedureParameter("p_actor_id", Integer.class, ParameterMode.IN);
        query.setParameter("p_actor_id", actorId);
        return (Actors) query.getSingleResult();
    }
}