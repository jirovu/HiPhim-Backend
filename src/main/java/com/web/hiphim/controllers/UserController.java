package com.web.hiphim.controllers;

import com.web.hiphim.models.User;
import com.web.hiphim.repositories.IUserRepository;
import com.web.hiphim.security.services.CookieProvider;
import com.web.hiphim.security.services.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private CookieProvider cookieProvider;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/greet")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public String greet() {
        return "Welcome to homepage";
    }

    /*
     * This method used to handle login user and response JWT TOKEN to client
     * */
    @PostMapping("/login")
    public void home(HttpServletResponse response,
                     @Valid @RequestBody User user) {
        var userExist = userRepository.findByUsername(user.getUsername());
        if (userExist != null && passwordEncoder.matches(user.getPassword(), userExist.getPassword())) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userExist.getUsername(), userExist.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            cookieProvider.create(response, cookieProvider.getCookieName(), token);
        }
    }

    @PostMapping("/register")
    public boolean register(@Valid @RequestBody User user) {
        try {
            if (userRepository.findByUsername(user.getUsername()) == null) {
                userRepository.insert(new User(user.getUsername(), passwordEncoder.encode(user.getPassword()),
                        user.getName(), user.getUrlAvt(), user.getRoles()));
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
