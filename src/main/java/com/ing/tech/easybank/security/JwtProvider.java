package com.ing.tech.EasyBank.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.tech.EasyBank.exception.UnauthorizedException;
import com.ing.tech.EasyBank.exception.UnequalRequestAndTokenUsername;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtProvider {

    private static final String AUTHORITIES = "authorities";
    private static final String SECRET = "secret_greu";
    private final ObjectMapper objectMapper;

    public JwtProvider() {
        this.objectMapper = new ObjectMapper();
    }

    public String generateToken(String username, Set<String> authorities, int ttl) {
        Date date = Date.from(ZonedDateTime.now().plusMinutes(ttl).toInstant());

        return Jwts.builder()
                .setSubject(username)
                .claim(AUTHORITIES, authorities)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .setExpiration(date)
                .compact();
    }

    public boolean validate(String token) {
        if (token.isBlank()) {
            throw new UnauthorizedException();
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token).getBody();

            /*if(!claims.getSubject().equals(username))
                throw new UnequalRequestAndTokenUsername("Username in token is different from username in body !");
            */
        } catch (JwtException e) {
            log.error(e.getMessage(), e);
            throw new UnauthorizedException();
        }

        return true;
    }

    public Authentication authentication(String token) throws JsonProcessingException {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token).getBody();

        Set<SimpleGrantedAuthority> grantedAuthorities =
                ((ArrayList<String>)claims.get(AUTHORITIES))
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());

        User user = new User(claims.getSubject(), "", grantedAuthorities);

        return new UsernamePasswordAuthenticationToken(user, "", grantedAuthorities);
    }
}
