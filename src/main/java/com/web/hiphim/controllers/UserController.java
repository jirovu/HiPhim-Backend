package com.web.hiphim.controllers;

import com.web.hiphim.models.Movie;
import com.web.hiphim.models.Supervisor;
import com.web.hiphim.models.User;
import com.web.hiphim.repositories.IMovieRepository;
import com.web.hiphim.repositories.ISupervisorRepository;
import com.web.hiphim.repositories.IUserRepository;
import com.web.hiphim.security.services.CookieProvider;
import com.web.hiphim.security.services.JwtTokenProvider;
import com.web.hiphim.services.app42api.App42Service;
import com.web.hiphim.services.mail.MailProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private ISupervisorRepository supervisorRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MailProvider mailProvider;
    @Autowired
    private App42Service app42Service;
    @Autowired
    private IMovieRepository movieRepository;

    @GetMapping("/greet")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public String greet() {
        return "Welcome to homepage";
    }

    /*
     * Handle forget password
     * Return True if email is valid
     * Otherwise return False
     * */
    @PostMapping("/forget-password")
    public boolean forgetPassword(@RequestBody Supervisor supervisor) {
        try {
            var userExist = userRepository.findByEmail(supervisor.getUserEmail());
            var supervisorExist = supervisorRepository.findByUserEmail(supervisor.getUserEmail());
            if (userExist != null) {
                if (supervisorExist == null) {
                    supervisorRepository.insert(supervisor);
                } else {
                    supervisorExist.setIdentifyCode(supervisor.getIdentifyCode());
                    supervisorExist.setCreatedTime(new Date());
                    supervisorRepository.save(supervisorExist);
                }
                mailProvider.sendMailTo(supervisor.getUserEmail(), supervisor.getIdentifyCode());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /*
     * Change password for User
     * Return True if successfully
     * Otherwise return False
     * */
    @PostMapping("/change-password")
    public boolean changePassword(@RequestBody Supervisor supervisor) {
        try {
            var supervisorExist = supervisorRepository.findByIdentifyCode(supervisor.getIdentifyCode());
            if (supervisorExist != null) {
                var userExist = userRepository.findByEmail(supervisorExist.getUserEmail());
                if (userExist != null) {
                    userExist.setPassword(passwordEncoder.encode(supervisor.getPassword()));
                    userRepository.save(userExist);
                    return true;
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /*
     * Check identify code if code is valid
     * Return True if successfully
     * Otherwise return False
     * */
    @PostMapping("/identify-code")
    public boolean identifyCode(@RequestBody Supervisor supervisor) {
        try {
            var supervisorExist = supervisorRepository.findByIdentifyCode(supervisor.getIdentifyCode());
            if (supervisorExist != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /*
     * This method used to handle login user and response JWT TOKEN to client
     * */
    @PostMapping("/login")
    public ResponseEntity<Boolean> home(HttpServletResponse response,
                                            @Valid @RequestBody User user) {
        var userExist = userRepository.findByEmail(user.getEmail());
        if (userExist != null && passwordEncoder.matches(user.getPassword(), userExist.getPassword())) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userExist.getEmail(), userExist.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            cookieProvider.create(response, cookieProvider.getCookieName(), token);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(true);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(false);
    }

    /*
     * Register new user
     * Return True if successfully
     * Otherwise return False
     * */
    @PostMapping("/register")
    public boolean register(@Valid @RequestBody User user) {
        try {
            if (userRepository.findByEmail(user.getEmail()) == null) {
                userRepository.insert(new User(user.getEmail(), passwordEncoder.encode(user.getPassword()),
                        user.getName(), user.getUrlAvt(), user.getRoles()));
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
