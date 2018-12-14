package io.piano.demo.security;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
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


    private final String tokenHeaderName;
    private final String tokenHeaderPrefix;
    private final String secretKey;
    private final long validityInMilliseconds;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(
            @Value("${security.jwt.token.header-name:Authorization}") String tokenHeaderName,
            @Value("${security.jwt.token.header-prefix:Bearer }") String tokenHeaderPrefix,
            @Value("${security.jwt.token.secret-key:secret}") String secretKey,
            @Value("${security.jwt.token.expire-time:3600000}") long validityInMilliseconds,
            UserDetailsService userDetailsService) {
        this.tokenHeaderName = tokenHeaderName;
        this.tokenHeaderPrefix = tokenHeaderPrefix;
        this.secretKey = secretKey;
        this.validityInMilliseconds = validityInMilliseconds;
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

    public Authentication getAuthentication(String token) {
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
        String bearerToken = req.getHeader(tokenHeaderName);
        if (bearerToken != null && bearerToken.startsWith(tokenHeaderPrefix)) {
            return bearerToken.substring(tokenHeaderPrefix.length() + 1);
        }
        return null;
    }

    public boolean notExpired(String token) {
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
