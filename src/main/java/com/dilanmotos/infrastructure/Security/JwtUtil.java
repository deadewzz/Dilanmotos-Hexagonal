package com.dilanmotos.infrastructure.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Metáfora: La llave maestra única del taller
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  
    private final long expireTime = 60000 * 15;

    // --- GENERAR EL TOKEN ---
    public String generateToken(String correo) {
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // --- EXTRAER NOMBRE (SUBJECT) ---
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // --- VALIDAR TOKEN ---
    public Boolean validateToken(String token, String correo) {
        final String username = extractUsername(token);
        return (username.equals(correo) && !isTokenExpired(token));
    }

    // --- EXTRAER CUALQUIER DATO (CLAIM) ---
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // --- LEER TODO EL CONTENIDO DEL TOKEN
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // --- VERIFICAR EXPIRACIoN ---
    private Boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}