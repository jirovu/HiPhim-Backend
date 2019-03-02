package com.web.hiphim.security.filters;

import com.web.hiphim.models.TokenBlacklist;
import com.web.hiphim.repositories.ITokenBlacklist;
import com.web.hiphim.security.services.CookieProvider;
import com.web.hiphim.security.services.CustomUserDetailsService;
import com.web.hiphim.security.services.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


/*
 * Authorize any request come to application
 * */
@Service
public class AuthorizationHeaderPerRequest extends OncePerRequestFilter {
    @Value("${server.servlet.session.cookie.name}")
    private String cookieName;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CookieProvider cookieProvider;
    @Autowired
    private ITokenBlacklist tokenBlacklist;

    /*
     * Handle any request and set authentication if valid
     * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            var jwtToken = getJwtTokenFromHeader(request);
//            var tokenInBlacklist = tokenBlacklist.findByToken(jwtToken);
            if (StringUtils.hasText(jwtToken)) {
//                tokenBlacklist.insert(new TokenBlacklist(jwtToken));
                UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                cookieProvider.updateCookie(response);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        filterChain.doFilter(request, response);

    }

    /*
     * Get jwt token from header
     * Return Token if request header is valid
     * Otherwise return null
     * */
    private String getJwtTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    /*
     * Get UsernamePasswordAuthenticationToken based on token
     * Return new instance if valid
     * Otherwise return null
     * */
    private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
        try {
            // Get user by token
            String email = Jwts.parser().setSigningKey(jwtTokenProvider.getSecret())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            // Call loadUserByUsername method to get Object User that contains USERNAME, PASSWORD AND AUTHORITIES
            var userDetails = customUserDetailsService.loadUserByUsername(email);

            return userDetails != null ?
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                    : null;
        } catch (Exception e) {
            return null;
        }
    }
}
