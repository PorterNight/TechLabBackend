package techlab.backend.security;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import techlab.backend.repository.jpa.security.UserSecurity;
import techlab.backend.service.exception.RestResponseException;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;
    private static final SecretKey ACCESS_TOKEN_SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final SecretKey REFRESH_TOKEN_SECRET_KEY = Jwts.SIG.HS256.key().build();

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public String createAccessToken(String username, String role) {

        return Jwts.builder()
                .claim("role", role)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10000 * 60 * 24))
                .signWith(ACCESS_TOKEN_SECRET_KEY).compact();
    }

    public String createRefreshToken(String username, String role) {

        return Jwts.builder()
                .claim("role", role)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10000 * 60 * 24))
                .signWith(REFRESH_TOKEN_SECRET_KEY).compact();
    }

    public boolean validateAccessToken(String token) {
        Jws<Claims> claimsJws;
        try {
            claimsJws = Jwts.parser()
                    .verifyWith(ACCESS_TOKEN_SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);
            boolean res = claimsJws.getPayload().getExpiration().after(new Date(System.currentTimeMillis()));

            log.info("JWT token is valid: " + res);
            return res;
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        } catch (JwtException ex) {
            log.info("JWT token is expired, SECRET_KEY: " + ACCESS_TOKEN_SECRET_KEY);
        }
        return false;
    }

    public String getUsernameFromRefreshToken(String token) {
        return Jwts.parser().verifyWith(REFRESH_TOKEN_SECRET_KEY).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(ACCESS_TOKEN_SECRET_KEY).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsernameFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // the token is after "Bearer "
        }
        return null;
    }
}