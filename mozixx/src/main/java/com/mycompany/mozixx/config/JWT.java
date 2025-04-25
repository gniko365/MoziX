package com.mycompany.mozixx.config;

import com.mycompany.mozixx.exception.ExceptionLogger;
import com.mycompany.mozixx.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat; // Use standard Java HexFormat

public class JWT {

    /*
     * eyJhbGciOiJIUzI1NiJ9.
     * eyJpc3MiOiJWaXN1ZWxzZSIsInN1YiI6Im1hbmFnZW1lbnQiLCJpZCI6MTI3LCJzY29wZSI6InVzZXIiLCJzc1RhZyI6IkBBQiIsImlhdCI6MTcyNDk1NzMwOSwiZXhwIjoxNzI1NTYyMTA5fQ.
     *
     * Wj7kKUIVtWpLHwEkghRqnAjkb0xO7GJ4mOpiEDgxrs0
     */

    private static final String SIGN = "09ce78e64c7d6667e04798aa897e2bbc194d0ce5d19aef677b4477ba0932d972";
    private static final byte[] SECRET = HexFormat.of().parseHex(SIGN); // Correct and standard hex decoding
    private static ExceptionLogger exceptionLogger = new ExceptionLogger(JWT.class);


    public static String createJWT(Users u) {
        Instant now = Instant.now();

        String token = Jwts.builder()
                .setIssuer("IAKK") // Token kiállítója
                .setSubject("valamit") // Token tárgya
                .claim("id", u.getId()) // Felhasználó ID
                .claim("role", u.getRole()) // Felhasználó szerepe
                .claim("Registration date", u.getRegistrationDate()) // Regisztráció dátuma
                .setIssuedAt(Date.from(now)) // Token kiállításának ideje
                .setExpiration(Date.from(now.plus(1, ChronoUnit.DAYS))) // Token lejárati ideje (1 nap)
                .signWith(
                        Keys.hmacShaKeyFor(SECRET), // Titkosítási kulcs
                        SignatureAlgorithm.HS256 // Titkosítási algoritmus
                )
                .compact();

        return token;
    }

    public static int validateJWT(String jwt) {
        try {
            Jws<Claims> result;
            result = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(SECRET)).parseClaimsJws(jwt);
            int id = result.getBody().get("id", Integer.class);
            // Removed the unnecessary database lookup in the validation
            return 1; // Token érvényes
        } catch (ExpiredJwtException e) {
            exceptionLogger.errorLog(e);
            return 3; //Akkor történik ha lejárt a JWT-k
        } catch (Exception e) {
            exceptionLogger.errorLog(e);
            return 2; //Ez akkor történik amikor egy érvénytelen tokent akarunk validáltatni
        }

    }

    public static Boolean isAdmin(String jwt){
        Jws<Claims> result;
        result = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(SECRET)).parseClaimsJws(jwt);

        Boolean isAdmin = result.getBody().get("isAdmin", Boolean.class);

        return isAdmin;
    }

    public static Integer getUserIdByToken(String jwt){
        Jws<Claims> result;
        result = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(SECRET)).parseClaimsJws(jwt);

        int userId = result.getBody().get("id", Integer.class);

        return userId;
    }

    public static String getUserRoleByToken(String jwt) {
    Jws<Claims> result;
    result = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(SECRET)).parseClaimsJws(jwt);
    String role = result.getBody().get("role", String.class);
    return role;
}
}