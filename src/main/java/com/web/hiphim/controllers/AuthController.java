package com.web.hiphim.controllers;

import com.web.hiphim.models.Supervisor;
import com.web.hiphim.models.User;
import com.web.hiphim.repositories.ISupervisorRepository;
import com.web.hiphim.repositories.IUserRepository;
import com.web.hiphim.security.services.CookieProvider;
import com.web.hiphim.security.services.JwtTokenProvider;
import com.web.hiphim.services.mail.MailProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/auth")
public class AuthController {
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

    /*
     * Handle forget password
     * Return True if email is valid
     * Otherwise return False
     * */
    @PostMapping("/forgetPassword")
    public ResponseEntity<Boolean> forgetPassword(@RequestBody Supervisor supervisor) {
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
                boolean isSuccessfully = mailProvider.sendMailTo(supervisor.getUserEmail(), supervisor.getIdentifyCode());
                if (isSuccessfully) {
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(true);
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(false);
        }
    }

    /*
     * Change password for User
     * Return True if successfully
     * Otherwise return False
     * */
    @PostMapping("/changePassword")
    public ResponseEntity<Boolean> changePassword(@RequestBody Supervisor supervisor) {
        try {
            var supervisorExist = supervisorRepository.findByIdentifyCode(supervisor.getIdentifyCode());
            if (supervisorExist != null) {
                var userExist = userRepository.findByEmail(supervisorExist.getUserEmail());
                if (userExist != null) {
                    userExist.setPassword(passwordEncoder.encode(supervisor.getPassword()));
                    userRepository.save(userExist);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(true);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(false);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(false);
        }
    }

    /*
     * Check identify code if code is valid
     * Return True if successfully
     * Otherwise return False
     * */
    @PostMapping("/identifyCode")
    public ResponseEntity<Boolean> identifyCode(@RequestBody Supervisor supervisor) {
        try {
            var supervisorExist = supervisorRepository.findByIdentifyCode(supervisor.getIdentifyCode());
            if (supervisorExist != null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(true);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(false);
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
    public ResponseEntity<Boolean> register(@Valid @RequestBody User user) {
        try {
            if (userRepository.findByEmail(user.getEmail()) == null) {
                userRepository.insert(new User(user.getEmail(), passwordEncoder.encode(user.getPassword()),
                        user.getName(), user.getUrlAvt(), user.getRoles()));
                return ResponseEntity.status(HttpStatus.OK)
                        .body(true);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(false);
        }
    }
}
