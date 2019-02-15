package com.web.hiphim.security.services;

import com.web.hiphim.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/*
 * Authenticate user
 * */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;

    /*
     * Return the User when input value is valid
     * Otherwise return null
     * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userExist = userRepository.findByUsername(username);

        if (userExist != null) {
            var authorities = userExist.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
            return new User(username, userExist.getPassword(), authorities);
        }

        return null;
    }
}
