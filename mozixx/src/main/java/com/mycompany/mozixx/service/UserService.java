package com.mycompany.mozixx.service;

import com.mycompany.mozixx.config.JWT;
import com.mycompany.mozixx.model.Users;
import com.mycompany.mozixx.utils.PasswordValidator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import org.json.JSONArray;
import org.json.JSONObject;



public class UserService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.iakk_backendVizsga_war_1.0-SNAPSHOTPU");
    private final Users layer = new Users();
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    
    
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

    if (!hasNumber) {
        errors.add("A jelszónak tartalmaznia kell legalább egy számot.");
    }
    if (!hasUpperCase) {
        errors.add("A jelszónak tartalmaznia kell legalább egy nagybetűt.");
    }
    if (!hasLowerCase) {
        errors.add("A jelszónak tartalmaznia kell legalább egy kisbetűt.");
    }
    if (!hasSpecialChar) {
        errors.add("A jelszónak tartalmaznia kell legalább egy speciális karaktert.");
    }

    return errors;
}
    
    public Users login_old(String email, String password) {
        return layer.loginUser(email, password);
    }
    
    public JSONObject loginhaha(String email, String password) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        int statusCode = 200;

        if (isValidEmail(email)) {
            Users modelResult = layer.loginUser(email, password);

            if (modelResult == null) {
                status = "modelException";
                statusCode = 500;
            } else {
                if (modelResult.getId() == null) {
                    status = "userNotFound";
                    statusCode = 417;
                } else {
                    JSONObject result = new JSONObject();
                    result.put("id", modelResult.getId());
                    result.put("email", modelResult.getEmail());
                    result.put("firstName", modelResult.getUsername());
                    result.put("jwt", JWT.createJWT(modelResult));

                    toReturn.put("result", result);
                }
            }

        } else {
            status = "invalidEmail";
            statusCode = 417;
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
    public org.json.JSONObject getAllUser() {
        org.json.JSONObject toReturn = new org.json.JSONObject();
        String status = "success";
        int statusCode = 200;
        List<Users> modelResult = layer.getAllUser();

        if (modelResult == null) {
            status = "ModelException";
            statusCode = 500;
        } else if (modelResult.isEmpty()) {
            status = "NoUsersFound";
            statusCode = 417;
        } else {
            JSONArray result = new JSONArray();
            
            for(Users actualUser : modelResult){
                org.json.JSONObject toAdd = new org.json.JSONObject();

                toAdd.put("id", actualUser.getId());
                toAdd.put("email", actualUser.getEmail());
                toAdd.put("Registration date", actualUser.getRegistration_date());
                
                result.put(toAdd);
            }
            
            toReturn.put("result", result);
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
    
    public org.json.JSONObject getUserById(Integer id){
        org.json.JSONObject toReturn = new org.json.JSONObject();
        String status = "success";
        int statusCode = 200;
        Users modelResult = new Users(id);
        
        if(modelResult.getEmail() == null){
            status = "UserNotFound";
            statusCode = 417;
        }else{
            org.json.JSONObject user = new org.json.JSONObject();
            
            user.put("id", modelResult.getId());
            user.put("email", modelResult.getEmail());
            user.put("Registration date", modelResult.getRegistration_date());
            
            toReturn.put("result", user);
        }
        
        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
    
    public JSONObject login(String email, String password) {
        JSONObject response = new JSONObject();
        EntityManager em = emf.createEntityManager();

        try {
            // Felhasználó keresése az e-mail cím alapján
            Users user = em.createQuery("SELECT u FROM Users u WHERE u.email = :email", Users.class)
                           .setParameter("email", email)
                           .getSingleResult();

            // Jelszó ellenőrzése
            if (user != null && user.getPassword().equals(password)) {
                response.put("statusCode", 200);
                response.put("message", "Login successful");
                response.put("user", new JSONObject(user)); // Felhasználó adatainak hozzáadása
            } else {
                response.put("statusCode", 401);
                response.put("message", "Hibás e-mail cím vagy jelszó.");
            }
        } catch (NoResultException e) {
            // Ha nincs ilyen felhasználó
            response.put("statusCode", 401);
            response.put("message", "Hibás e-mail cím vagy jelszó.");
        } catch (Exception e) {
            System.err.println("Hiba a bejelentkezés során: " + e.getLocalizedMessage());
            response.put("statusCode", 500);
            response.put("message", "Internal server error");
        } finally {
            em.close();
        }

        return response;
    }
    private Users loginUser(String email, String password) {
    EntityManager em = emf.createEntityManager();
    Users toReturn = new Users();

    try {
        StoredProcedureQuery spq = em.createStoredProcedureQuery("login");

        spq.registerStoredProcedureParameter("emailIN", String.class, ParameterMode.IN);
        spq.registerStoredProcedureParameter("passwordIN", String.class, ParameterMode.IN);

        spq.setParameter("emailIN", email);
        spq.setParameter("passwordIN", password);

        spq.execute();

        List<Object[]> resultList = spq.getResultList();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        for (Object[] o : resultList) {
            Users u = new Users(
                Integer.valueOf(o[0].toString()), // id
                o[2].toString(),                 // email
                o[1].toString(),                 // username
                o[3].toString(),                 // password
                o[4].toString(),                 // role
                Boolean.parseBoolean(o[5].toString()), // active
                o.length > 6 ? (o[6] == null ? null : formatter.parse(o[6].toString())) : null // createdAt
            );
            toReturn = u;
        }
    } catch (NumberFormatException | ParseException e) {
        System.err.println("Hiba: " + e.getLocalizedMessage());
    } finally {
        em.clear();
        em.close();
    }

    return toReturn;
}

    private JSONObject userToJSON(Users user) {
        JSONObject json = new JSONObject();
        json.put("id", user.getId());
        json.put("email", user.getEmail());
        json.put("username", user.getUsername());
        json.put("password", user.getPassword());
        json.put("role", user.getRole());
        json.put("Registration Date", user.getRegistration_date());
        return json;
    }
    
    public JSONObject registerUser(Users user) {
    JSONObject response = new JSONObject();

    // E-mail cím ellenőrzése
    if (!isValidEmail(user.getEmail())) {
        response.put("statusCode", 400);
        response.put("message", "Érvénytelen e-mail cím.");
        return response;
    }

    // Jelszó ellenőrzése
    List<String> passwordErrors = isValidPassword(user.getPassword());
    if (!passwordErrors.isEmpty()) {
        response.put("statusCode", 400);
        response.put("message", "A jelszó nem felel meg a követelményeknek:");
        response.put("errors", passwordErrors);
        return response;
    }

    // Ellenőrizze, hogy az e-mail cím már regisztrálva van-e
    if (isEmailAlreadyRegistered(user.getEmail())) {
        response.put("statusCode", 400);
        response.put("message", "Az e-mail cím már regisztrálva van.");
        return response;
    }

    // Ellenőrizze, hogy a felhasználónév már használatban van-e
    if (isUsernameAlreadyTaken(user.getUsername())) {
        response.put("statusCode", 400);
        response.put("message", "A felhasználónév már foglalt.");
        return response;
    }

    EntityManager em = emf.createEntityManager();

    try {
        em.getTransaction().begin();
        em.persist(user); // Az új felhasználó mentése az adatbázisba
        em.getTransaction().commit();

        response.put("statusCode", 200);
        response.put("message", "User registered successfully");
    } catch (Exception e) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        System.err.println("Hiba a regisztráció során: " + e.getLocalizedMessage());
        response.put("statusCode", 500);
        response.put("message", "Internal server error");
    } finally {
        em.close();
    }

    return response;
}

public JSONObject registerAdmin(Users user, String jwt) {
    JSONObject response = new JSONObject();
    // Admin regisztrációs logika
    response.put("statusCode", 200);
    response.put("message", "Admin registered successfully");
    return response;
}

    public boolean isEmailAlreadyRegistered(String email) {
    EntityManager em = emf.createEntityManager();
    try {
        Long count = em.createQuery("SELECT COUNT(u) FROM Users u WHERE u.email = :email", Long.class)
                       .setParameter("email", email)
                       .getSingleResult();
        return count > 0;
    } finally {
        em.close();
    }
}

    public boolean isUsernameAlreadyTaken(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(u) FROM Users u WHERE u.username = :username", Long.class)
                           .setParameter("username", username)
                           .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}