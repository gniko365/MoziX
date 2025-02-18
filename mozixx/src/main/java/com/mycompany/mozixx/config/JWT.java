package com.mycompany.mozixx.config;

import com.mycompany.mozixx.exception.ExceptionLogger;
import com.mycompany.mozixx.model.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

public class JWT {

    private static final String SIGN = "09ce78e64c7d6667e04798aa897e2bbc194d0ce5d19aef677b4477ba0932d972";
    private static final byte[] SECRET = DatatypeConverter.parseHexBinary(SIGN); // HEX dekódolás
    private static ExceptionLogger exceptionLogger = new ExceptionLogger(JWT.class);

    public static String createJWT(Users u) {
        Instant now = Instant.now();

        String token = Jwts.builder()
                .setIssuer("IAKK")
                .setSubject("user_auth")
                .claim("id", u.getId())
                .claim("isAdmin", u.isAdmin())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.DAYS)))
                .signWith(Keys.hmacShaKeyFor(SECRET), SignatureAlgorithm.HS256) // Helyes aláírás
                .compact();

        return token;
    }

    public static int validateJWT(String jwt) {
        try {
            Jws<Claims> result = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET))
                    .parseClaimsJws(jwt);

            int id = result.getBody().get("id", Integer.class);

            Users u = Users.findById(id); // Validáljuk, hogy a user létezik
            return (u != null) ? 1 : 2;

        } catch (JwtException e) {
            exceptionLogger.errorLog(e);
            return 3;
        }
    }

    public static Boolean isAdmin(String jwt) {
        try {
            Jws<Claims> result = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET))
                    .parseClaimsJws(jwt);

            Boolean isAdmin = result.getBody().get("isAdmin", Boolean.class);
            return (isAdmin != null) ? isAdmin : false; // Ha nincs, akkor false

        } catch (JwtException e) {
            exceptionLogger.errorLog(e);
            return false;
        }
    }

    public static Integer getUserIdByToken(String jwt) {
        try {
            Jws<Claims> result = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET))
                    .parseClaimsJws(jwt);

            return result.getBody().get("id", Integer.class);

        } catch (JwtException e) {
            exceptionLogger.errorLog(e);
            return null;
        }
    }
}
