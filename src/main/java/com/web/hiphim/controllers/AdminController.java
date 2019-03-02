package com.web.hiphim.controllers;

import com.web.hiphim.models.Movie;
import com.web.hiphim.repositories.IMovieRepository;
import com.web.hiphim.repositories.IUserRepository;
import com.web.hiphim.services.app42api.App42Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/getAllMovies")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(movieRepository.findAll());
    }

    @PutMapping("/editMovie")
    public ResponseEntity<List<Movie>> editMovie(@RequestBody Movie movie) {
        var movieExist = movieRepository.findByMovieId(movie.getId());
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
}
