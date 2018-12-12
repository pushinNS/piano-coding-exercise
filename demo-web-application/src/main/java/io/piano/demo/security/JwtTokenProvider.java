package io.piano.demo.security;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String TOKEN_HEADER_NAME = "Authorization";
    private static final String TOKEN_HEADER_PREFIX = "Bearer ";
    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey;
    @Value("${security.jwt.token.expire-length:3600000}")   // 1h
    private long validityInMilliseconds;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public String createToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Claims claims = Jwts.claims().setSubject(userPrincipal.getUsername());
        claims.put("roles", userPrincipal.getAuthorities());
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(extractUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, EMPTY_STRING,
                userDetails.getAuthorities());
    }

    private String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(TOKEN_HEADER_NAME);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_HEADER_PREFIX)) {
            return bearerToken.substring(TOKEN_HEADER_PREFIX.length());
        }
        return null;
    }

    boolean notExpired(String token) {
        try {
            final Date now = new Date();
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(now);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Expired or invalid JWT token");
        }
        return false;
    }
}
