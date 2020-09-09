package com.lk.taskmanager.security;

import com.lk.taskmanager.entities.UserEntity;
import com.lk.taskmanager.services.auth.AuthUserMapper;
import com.lk.taskmanager.services.auth.dtos.AuthUserDTO;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.token.secret-key:secret}")
    private String secretKey;

    @Value("${app.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds;

    private final CustomUserDetailsService customUserDetailsService;

    public JwtTokenProvider(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(AuthUserDTO authUser) {
        Claims claims = Jwts.claims().setSubject(authUser.getUsername());
        claims.put("userId", String.valueOf(authUser.getId()));
        claims.put("fullname", authUser.getFullName());
        claims.put("role", authUser.getRole());
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public AuthUserDTO parseToken(String token, HttpServletRequest httpServletRequest) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            AuthUserDTO authUser = new AuthUserDTO();
            authUser.setUsername(body.getSubject());
            authUser.setId(Long.parseLong((String) body.get("userId")));
            authUser.setRole((String) body.get("role"));
            return authUser;
        } catch (JwtException | ClassCastException e) {
            log.error("Error while parsing AutherUser Info from JWT token");
            httpServletRequest.setAttribute("invalid", "Invalid JWT token");
            return null;
        }
    }

    public Authentication getAuthentication(String token, HttpServletRequest servletRequest) {
        AuthUserDTO authUserDTO = parseToken(token, servletRequest);
        UserEntity userEntity = AuthUserMapper.mapToAuthUserEntity(authUserDTO);
        CustomUserDetails customUserDetail = new CustomUserDetails(userEntity);
        return new UsernamePasswordAuthenticationToken(customUserDetail, "", customUserDetail.getAuthorities());
    }

    public String getTokenFromRequestHeader(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.split(" ")[1];
        }
        return null;
    }

    public boolean validateToken(String token, HttpServletRequest httpServletRequest) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            httpServletRequest.setAttribute("expired", "Expired JWT token");
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            log.error("Invalid JWT token");
            ex.printStackTrace();
            httpServletRequest.setAttribute("invalid", "Invalid JWT token");
        }
        return false;
    }

}
