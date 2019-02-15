package com.web.hiphim.security.filters;

import com.web.hiphim.security.services.CustomUserDetailsService;
import com.web.hiphim.security.services.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class AuthorizationHeaderPerRequest extends OncePerRequestFilter {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            var jwtToken = getJwtTokenFromHeader(request);
            if (StringUtils.hasText(jwtToken)) {
                UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        filterChain.doFilter(request, response);

    }

    private String getJwtTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
        try {
            // Get user by token
            String username = Jwts.parser().setSigningKey(jwtTokenProvider.getSecret())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            // Call loadUserByUsername method to get Object User that contains USERNAME, PASSWORD AND AUTHORITIES
            var userDetails = customUserDetailsService.loadUserByUsername(username);

            return userDetails != null ?
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                    : null;
        } catch (Exception e) {
            return null;
        }
    }
}
