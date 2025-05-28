package com.mycompany.mozixx.service;

import com.mycompany.mozixx.config.JWT;
import com.mycompany.mozixx.model.Users;
import com.mycompany.mozixx.model.Users.Role;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

// Spring Framework annotációk eltávolítva
//@Service
public class UserService {
    private final EntityManagerFactory emf;
    private final EntityManager em;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    
    public UserService() {
        this.emf = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");
        this.em = emf.createEntityManager();
    }
    
    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    public static List<String> isValidPassword(String password) {
        List<String> errors = new ArrayList<>();
        if (password.length() < 8) {
            errors.add("A jelszónak legalább 8 karakter hosszúnak kell lennie.");
        }

        boolean hasNumber = false;
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if ("!@#$%^&*()_+-=[]{}|;':,.<>?/`~".indexOf(c) != -1) {
                hasSpecialChar = true;
            }
        }

        if (!hasNumber) errors.add("A jelszónak tartalmaznia kell legalább egy számot.");
        if (!hasUpperCase) errors.add("A jelszónak tartalmaznia kell legalább egy nagybetűt.");
        if (!hasLowerCase) errors.add("A jelszónak tartalmaznia kell legalább egy kisbetűt.");
        if (!hasSpecialChar) errors.add("A jelszónak tartalmaznia kell legalább egy speciális karaktert.");

        return errors;
    }
    
    public JSONObject getAllUser() {
        JSONObject toReturn = new JSONObject();
        try {
            beginTransaction();
            List<Users> users = getAllUsers();
            commitTransaction();
            
            JSONArray result = new JSONArray();
            for (Users user : users) {
                JSONObject userJson = userToJSON(user);
                result.put(userJson);
            }
            
            toReturn.put("status", "success");
            toReturn.put("statusCode", 200);
            toReturn.put("result", result);
        } catch (Exception e) {
            rollbackTransaction();
            toReturn.put("status", "error");
            toReturn.put("statusCode", 500);
            toReturn.put("message", e.getMessage());
        }
        return toReturn;
    }
    
    public JSONObject getUserById(Integer id) {
        JSONObject toReturn = new JSONObject();
        try {
            beginTransaction();
            Users user = em.find(Users.class, id);
            commitTransaction();
            
            if (user == null) {
                toReturn.put("status", "not_found");
                toReturn.put("statusCode", 404);
            } else {
                toReturn.put("status", "success");
                toReturn.put("statusCode", 200);
                toReturn.put("result", userToJSON(user));
            }
        } catch (Exception e) {
            rollbackTransaction();
            toReturn.put("status", "error");
            toReturn.put("statusCode", 500);
            toReturn.put("message", e.getMessage());
        }
        return toReturn;
    }
    
    public JSONObject login(String username, String password) {
        JSONObject response = new JSONObject();
        try {
            beginTransaction();
            TypedQuery<Users> query = em.createQuery(
                    "SELECT u FROM Users u WHERE u.username = :username",
                    Users.class);
            query.setParameter("username", username);
            Users user = query.getSingleResult();
            commitTransaction();

            if (user != null) {
                // Jelszó ellenőrzése a BCrypt segítségével
                if (BCrypt.checkpw(password, user.getPassword())) {
                    response.put("status", "success");
                    response.put("statusCode", 200);
                    response.put("result", userToJSON(user));
                    response.put("jwt", JWT.createJWT(user));
                } else {
                    response.put("status", "unauthorized");
                    response.put("statusCode", 401);
                    response.put("message", "Hibás felhasználónév vagy jelszó");
                }
            } else {
                response.put("status", "unauthorized");
                response.put("statusCode", 401);
                response.put("message", "Hibás felhasználónév vagy jelszó");
            }
        } catch (NoResultException e) {
            response.put("status", "unauthorized");
            response.put("statusCode", 401);
            response.put("message", "Hibás felhasználónév vagy jelszó");
        } catch (Exception e) {
            rollbackTransaction();
            response.put("status", "error");
            response.put("statusCode", 500);
            response.put("message", e.getMessage());
        }
        return response;
    }

    public JSONObject registerUser(Users user) {
        JSONObject response = new JSONObject();
        EntityTransaction transaction = null;
        
        try {
            transaction = em.getTransaction();
            transaction.begin();

            // 1. Validációk
            if (isEmailAlreadyRegistered(user.getEmail())) {
                response.put("status", "error");
                response.put("statusCode", 409);
                response.put("message", "Az email cím már regisztrálva van");
                transaction.rollback();
                return response;
            }
            
            if (isUsernameAlreadyTaken(user.getUsername())) {
                response.put("status", "error");
                response.put("statusCode", 409);
                response.put("message", "A felhasználónév már foglalt");
                transaction.rollback();
                return response;
            }

            // 2. Jelszó hash-elés
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            user.setRegistrationDate(new Date());
            user.setRole(Role.user);

            // 3. Mentés
            em.persist(user);
            transaction.commit();

            response.put("status", "success");
            response.put("statusCode", 201);
            response.put("message", "Sikeres regisztráció");
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            response.put("status", "error");
            response.put("statusCode", 500);
            response.put("message", "Szerverhiba történt: " + e.getMessage());
        }
        
        return response;
    }
    
    public JSONObject registerAdmin(Users user) {
    JSONObject response = new JSONObject();
    EntityTransaction transaction = null;

    try {
        transaction = em.getTransaction();
        transaction.begin();

         if (isEmailAlreadyRegistered(user.getEmail())) {
            response.put("status", "error");
            response.put("statusCode", 409);
            response.put("message", "Az email cím már regisztrálva van");
            transaction.rollback();
            return response;
        }
        
        if (isUsernameAlreadyTaken(user.getUsername())) {
            response.put("status", "error");
            response.put("statusCode", 409);
            response.put("message", "A felhasználónév már foglalt");
            transaction.rollback();
            return response;
        }

        // Jelszó hash-elés
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setRegistrationDate(new Date());
        user.setRole(Role.admin); // Beállítjuk a szerepkört adminra

        em.persist(user);
        transaction.commit();

        response.put("status", "success");
        response.put("statusCode", 201);
        response.put("message", "Admin felhasználó sikeresen regisztrálva");

    } catch (Exception e) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
        response.put("status", "error");
        response.put("statusCode", 500);
        response.put("message", "Szerverhiba történt: " + e.getMessage());
    }

    return response;
}


    public boolean isEmailAlreadyRegistered(String email) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM Users u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    public boolean isUsernameAlreadyTaken(String username) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM Users u WHERE u.username = :username", Long.class);
        query.setParameter("username", username);
        return query.getSingleResult() > 0;
    }
    
    public void updateUser(int userId, String username, String email, String password, Role role) {
        try {
            beginTransaction();
            Users user = em.find(Users.class, userId);
            if (user != null) {
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setRole(role);
                em.merge(user);
            }
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
}

    // UserService.java

public JSONObject deleteUser(int userId, String password) {
    JSONObject response = new JSONObject();
    EntityTransaction transaction = null;
    try {
        transaction = em.getTransaction();
        transaction.begin();

        Users user = em.find(Users.class, userId);
        if (user == null) {
            response.put("status", "not_found");
            response.put("statusCode", 404);
            response.put("message", "Felhasználó nem található");
            return response;
        }

        // Jelszó ellenőrzése
        if (!BCrypt.checkpw(password, user.getPassword())) {
            response.put("status", "unauthorized");
            response.put("statusCode", 401);
            response.put("message", "Hibás jelszó");
            return response;
        }

        em.remove(user);
        transaction.commit();

        response.put("status", "success");
        response.put("statusCode", 200);
        response.put("message", "Felhasználó sikeresen törölve");

    } catch (Exception e) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
        response.put("status", "error");
        response.put("statusCode", 500);
        response.put("message", "Szerverhiba történt: " + e.getMessage());
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    return response;
}
    
    public List<Users> getAllUsers() {
        return em.createQuery("SELECT u FROM Users u", Users.class).getResultList();
}
    
    public EntityManager getEntityManager() {
        return em;
    }

    public void beginTransaction() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    public void commitTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    public void rollbackTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    public void close() {
        try {
            if (em != null && em.isOpen()) {
                rollbackTransaction();
                em.close();
            }
        } finally {
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }
    
    private JSONObject userToJSON(Users user) {
        JSONObject json = new JSONObject();
        json.put("id", user.getUserId());
        json.put("username", user.getUsername());
        json.put("email", user.getEmail());
        json.put("role", user.getRole().name());
        json.put("registrationDate", user.getRegistrationDate());
        return json;
    }
    
     public JSONObject updateUsername(Integer userId, String currentPassword, String newUsername) {
        JSONObject response = new JSONObject();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        
        try {
            tx = em.getTransaction();
            tx.begin();

            // 1. Felhasználó keresése ID alapján
            Users user = em.find(Users.class, userId);
            if (user == null) {
                response.put("status", "error");
                response.put("message", "User not found");
                return response;
            }

            // 2. Jelszó ellenőrzése
            if (!BCrypt.checkpw(currentPassword, user.getPassword())) {
                response.put("status", "error");
                response.put("message", "Incorrect password");
                return response;
            }

            // 3. Felhasználónév validációk
            if (newUsername == null || newUsername.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Username cannot be empty");
                return response;
            }

            // 4. Egyediség ellenőrzés
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM Users u WHERE u.username = :username AND u.userId != :userId",
                Long.class);
            query.setParameter("username", newUsername);
            query.setParameter("userId", userId);
            
            if (query.getSingleResult() > 0) {
                response.put("status", "error");
                response.put("message", "Username already taken");
                return response;
            }

            // 5. Módosítás végrehajtása
            user.setUsername(newUsername);
            em.merge(user);
            tx.commit();

            response.put("status", "success");
            response.put("message", "Username updated successfully");
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            response.put("status", "error");
            response.put("message", "Error: " + e.getMessage());
        } finally {
            em.close();
        }
        
        return response;
    }
}

