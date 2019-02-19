package com.web.hiphim.controllers;

import com.web.hiphim.models.Movie;
import com.web.hiphim.repositories.IMovieRepository;
import com.web.hiphim.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private IMovieRepository movieRepository;
    @Autowired
    private IUserRepository userRepository;

    @GetMapping("")
    public ResponseEntity<List<Movie>> home() {
        List<Movie> movies = movieRepository.findAll();
        if (movies != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(movies);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(movies);
    }

    @GetMapping("/personal/{userId}")
    public ResponseEntity<Optional<Movie>> user(@PathVariable String userId, @RequestParam("id") String id) {
        var userExist = userRepository.findById(userId);
        var movieExist = movieRepository.findById(id);
        if (userExist != null && movieExist != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(movieExist);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }
}
