package com.mycompany.mozixx.service;

import com.mycompany.mozixx.model.Ratings;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;

public class RatingService {

    private EntityManagerFactory emf;
    private EntityManager em;

    public RatingService() {
        emf = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");
        em = emf.createEntityManager();
    }

    public void addRating(int userId, int movieId, int rating, String review) {
        try {
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
            query.execute();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
            emf.close();
        }
    }
}