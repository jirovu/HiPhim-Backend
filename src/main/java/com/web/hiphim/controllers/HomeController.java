package com.web.hiphim.controllers;

import com.web.hiphim.models.Comment;
import com.web.hiphim.models.Movie;
import com.web.hiphim.models.User;
import com.web.hiphim.repositories.ICommentRepository;
import com.web.hiphim.repositories.IHeraRepository;
import com.web.hiphim.repositories.IMovieRepository;
import com.web.hiphim.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private IMovieRepository movieRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IHeraRepository heraRepository;
    @Autowired
    private ICommentRepository commentRepository;

    /*
    * Get all movies
    * Return list of movies
    * */
    @GetMapping("/getAllMovies")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        if (movies != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(movies);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(movies);
    }

    /*
    * Get movies by both Category and Name
    * Return the movie if param is valid
    * Otherwise return false
    * */
    @GetMapping("/getMoviesByCategoryAndName")
    public ResponseEntity<List<Movie>> getMoviesByCategory(@Valid @RequestParam("category") String category,
                                                           @RequestParam("name") String name) {
        var result = movieRepository.findByCategoryAndName(category, name);
        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    /*
    * Get movies by name
    * */
    @GetMapping("/getMoviesByName")
    public ResponseEntity<List<Movie>> getMoviesByName(@Valid @RequestParam("name") String name) {
        var result = movieRepository.findByName(name);
        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    /*
    * Get movie by userId
    * Return the movie if the movie exists
    * Otherwise return null
    * */
    @GetMapping("/personal/{userId}")
    public ResponseEntity<Movie> user(@PathVariable String userId, @RequestParam("id") String id) {
        var userExist = userRepository.findByUserId(userId);
        var movieExist = movieRepository.findByMovieId(id);
        if (userExist != null && movieExist != null && movieExist.isApproved()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(movieExist);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    /*
    * Get list of movie by userId and movieId
    * */
    @GetMapping("/watch/{userId}")
    public ResponseEntity<List<Movie>> getMoviesByUserId(@PathVariable String userId,
                                                         @RequestParam("id") String movieId) {
        List<Movie> movies = movieRepository.findAllMoviesByUserId(userId).stream()
                .filter(movie -> !movie.getId().equals(movieId))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK)
                .body(movies);
    }

    /*
    * Get movies by category
    * Return the movie if the movie exists
    * Otherwise Return null
    * */
    @GetMapping("/getMoviesByCategory")
    public ResponseEntity<List<Movie>> getMoviesByCategory(@RequestParam("category") String category) {
        var moviesByCategory = movieRepository.findByCategory(category);
        if (moviesByCategory != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(moviesByCategory);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    /*
    * Get the answer based on the corresponding question
    * Return the answer
    * */
    @PostMapping("/getAns")
    public ResponseEntity<String> getAnswer(@RequestBody String ask) {
        Random random = new Random();
        List<String> answers = new ArrayList<>() {
            {
                heraRepository.findAnsByAsk(ask.toLowerCase()).forEach(hera -> add(hera.getAns()));
            }
        };

        try {
            if (answers.isEmpty()) {
                if (ask.contains(" ")) {
                    var fistWord = ask.substring(0, ask.indexOf(" "));
                    answers = new ArrayList<>() {
                        {
                            heraRepository.findAnsByAsk(fistWord).forEach(hera -> add(hera.getAns()));
                        }
                    };
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(answers.get(random.nextInt(answers.size())));
        } catch (Exception e) {
            answers = new ArrayList<>() {
                {
                    heraRepository.findAllByLimit(PageRequest.of(random.nextInt(100), 100))
                            .forEach(hera -> add(hera.getAns()));
                }
            };
            return ResponseEntity.status(HttpStatus.OK).body(answers.get(random.nextInt(answers.size())));
        }
    }

    /*
    * Get all comments of movie based on correspondence movieId
    * */
    @GetMapping("/getAllComments")
    public ResponseEntity<List<Comment>> getAllComment(@RequestParam("movieId") String movieId) {
        var comments = commentRepository.findAllByMovieId(movieId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(comments);
    }

    /*
    * Get User by userId
    * Return the user if userId is valid
    * Otherwise Return false
    * */
    @GetMapping("/getUserByUserId")
    public ResponseEntity<User> getUserByUserId(@RequestParam("userId") String userId) {
        var userExist = userRepository.findByUserId(userId);
        if (userExist != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userExist);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    /*
    * Get 8 movie limit
    * */
    @GetMapping("/getLimit8Movies")
    public ResponseEntity<List<Movie>> getLimit8Movies() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(movieRepository.findLimitMovies(PageRequest.of(0, 8)));
    }
}
