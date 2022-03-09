package com.ing.tech.EasyBank.security;

import com.ing.tech.EasyBank.exception.UnauthorizedException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class AuthorizationHeadersFilter implements Filter {

    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getServletPath();

/*        String collect = httpRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String[] tokens = collect.split("\"");
        String username = tokens[3];*/


        try {
            String token = resolveToken(httpRequest);
            if (path != null && !path.contains("/users") && jwtProvider.validate(token)) {
                Authentication authentication = jwtProvider.authentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please pass a valid jwt token!");
        } catch (AccessDeniedException e) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Please pass a valid jwt token!");
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");

        if(jwt == null || jwt.isBlank() || !jwt.startsWith("Bearer ")) {
            return "";
        }

        return jwt.substring(7);
    }
}

