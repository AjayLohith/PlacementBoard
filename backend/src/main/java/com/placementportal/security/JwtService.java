package com.placementportal.security;

import com.placementportal.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private SecretKey signingKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpiration().toMillis());
        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            log.debug("Could not parse JWT", e);
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.debug("Invalid JWT: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.debug("Expired JWT: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.debug("Unsupported JWT: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.debug("JWT claims empty: {}", ex.getMessage());
        }
        return false;
    }
}
