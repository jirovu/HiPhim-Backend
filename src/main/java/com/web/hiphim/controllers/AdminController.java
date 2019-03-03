package com.web.hiphim.controllers;

import com.web.hiphim.models.Log;
import com.web.hiphim.models.Movie;
import com.web.hiphim.repositories.ILogRepository;
import com.web.hiphim.repositories.IMovieRepository;
import com.web.hiphim.repositories.IUserRepository;
import com.web.hiphim.services.app42api.App42Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    @Autowired
    private IMovieRepository movieRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private App42Service app42Service;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ILogRepository logRepository;

    @GetMapping("/getAllMovies")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(movieRepository.findAll());
    }

    @GetMapping("/getAllLogs")
    public ResponseEntity<List<Log>> getAllLogs() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(logRepository.findAll());
    }

    @GetMapping("/getAllNotApprovedMovies")
    public ResponseEntity<List<Movie>> getAllNotApprovedMovies() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(movieRepository.findAllNotApprovedMovies());
    }

    @GetMapping("/personal/{userId}")
    public ResponseEntity<Movie> user(@PathVariable String userId, @RequestParam("id") String id) {
        var userExist = userRepository.findByUserId(userId);
        Movie movieExist = movieRepository.findByIdMovie(id);
        if (userExist != null && movieExist != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(movieExist);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    @PutMapping("/editMovie")
    public ResponseEntity<List<Movie>> editMovie(@RequestBody Movie movie) {
        var movieExist = movieRepository.findByIdMovie(movie.getId());
        if (movieExist != null) {
            movieExist.setName(movie.getName());
            movieExist.setDescription(movie.getDescription());
            movieRepository.save(movieExist);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(movieRepository.findAll());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    @PutMapping("/browseMovie")
    public ResponseEntity<List<Movie>> browseMovie(@RequestBody Movie movie){
        var movieExist = movieRepository.findByIdMovie(movie.getId());
        if (movieExist != null) {
            movieExist.setApproved(true);
            movieRepository.save(movieExist);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(movieRepository.findAllNotApprovedMovies());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    @PostMapping("/deleteMovie")
    public ResponseEntity<List<Movie>> deleteMovie(@RequestBody Movie movie) {
        var movieExist = movieRepository.findByMovieId(movie.getId());
        if (movieExist != null) {
            movieRepository.delete(movieExist);

            var email = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            app42Service.removeFileByUser(movie.getName(), email);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(movieRepository.findAll());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<Boolean> changePassword(@RequestBody com.web.hiphim.models.User user) {
        var email = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        var userExist = userRepository.findByEmail(email);
        if (userExist == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(false);
        }
        userExist.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userExist);
        return ResponseEntity.status(HttpStatus.OK)
                .body(true);
    }
}
