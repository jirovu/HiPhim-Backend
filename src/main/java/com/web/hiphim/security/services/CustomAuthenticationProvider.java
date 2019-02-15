package com.web.hiphim.security.services;

import com.web.hiphim.models.User;
import com.web.hiphim.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/*
 * Fetching user information from repository (DB, 3th party)
 * Uses this information to validate credentials (thong tin dang nhap)
 * */
@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private IUserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        User userExist = userRepository.findByUsername(username);
        if (userExist != null && password.equals(userExist.getPassword())) {
            var authorities = userExist.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());

            return new UsernamePasswordAuthenticationToken(username, password, authorities);
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
