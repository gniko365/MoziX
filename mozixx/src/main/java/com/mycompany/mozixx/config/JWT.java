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

public class JWT {

    /*
        eyJhbGciOiJIUzI1NiJ9.
    eyJpc3MiOiJWaXN1ZWxzZSIsInN1YiI6Im1hbmFnZW1lbnQiLCJpZCI6MTI3LCJzY29wZSI6InVzZXIiLCJzc1RhZyI6IkBBQiIsImlhdCI6MTcyNDk1NzMwOSwiZXhwIjoxNzI1NTYyMTA5fQ.
    
    Wj7kKUIVtWpLHwEkghRqnAjkb0xO7GJ4mOpiEDgxrs0
     */
    
    private static final String SIGN = "09ce78e64c7d6667e04798aa897e2bbc194d0ce5d19aef677b4477ba0932d972";
    private static final byte[] SECRET = Base64.getDecoder().decode(SIGN);
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
        Jws<Claims> result = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET))
                .parseClaimsJws(jwt);

        int id = result.getBody().get("id", Integer.class);
        String role = result.getBody().get("role", String.class); // Példa: szerep ellenőrzése
        String registrationDate = result.getBody().get("Registration date", String.class); // Példa: regisztráció dátum ellenőrzése

        // További ellenőrzések, ha szükséges
        return 1; // Token érvényes
    } catch (ExpiredJwtException e) {
        exceptionLogger.errorLog(e);
        return 3; // Token lejárt
    } catch (Exception e) {
        exceptionLogger.errorLog(e);
        return 2; // Token érvénytelen
    }
}
    
    public static Boolean role(String jwt){
        Jws<Claims> result;
        result = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(SECRET)).parseClaimsJws(jwt);
        
        Boolean role = result.getBody().get("role", Boolean.class);
        
        return role;
    }
    
    public static Integer getUserIdByToken(String jwt){
        Jws<Claims> result;
        result = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(SECRET)).parseClaimsJws(jwt);
        
        int userId = result.getBody().get("id", Integer.class);
        
        return userId;
    }

}