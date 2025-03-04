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
    
    
    
}