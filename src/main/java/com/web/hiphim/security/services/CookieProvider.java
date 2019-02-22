package com.web.hiphim.security.services;

import com.web.hiphim.repositories.IUserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CookieProvider {
    @Value("${server.servlet.session.cookie.max-age}")
    private int cookieAge;
    @Value("${server.servlet.session.cookie.name}")
    private String cookieName;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    /*
     * Create and setup cooke per request
     * */
    public void create(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(cookieAge);
        cookie.setPath("/");
        cookie.setValue(value);
        response.addCookie(cookie);
    }

    /*
     * Update expiration time for JWT Token
     * */
    public void updateCookie(HttpServletResponse response) {
        var username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        var userExist = userRepository.findByEmail(username);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userExist.getEmail(), userExist.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var newtoken = jwtTokenProvider.generateToken(authentication);
        create(response, cookieName, newtoken);
    }
}
