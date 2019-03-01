package com.web.hiphim.controllers;

import com.web.hiphim.models.Comment;
import com.web.hiphim.models.Movie;
import com.web.hiphim.models.User;
import com.web.hiphim.repositories.ICommentRepository;
import com.web.hiphim.repositories.IMovieRepository;
import com.web.hiphim.repositories.IUserRepository;
import com.web.hiphim.services.app42api.App42Service;
import com.web.hiphim.services.app42api.UploadHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("user")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private UploadHandler uploadHandler;
    @Autowired
    private IMovieRepository movieRepository;
    @Autowired
    private ICommentRepository commentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private App42Service app42Service;

    @PostMapping("/uploadFile")
    public ResponseEntity<Boolean> uploadFile(@RequestParam("file") MultipartFile file,
                                              @RequestParam("description") String description,
                                              @RequestParam("category") String category) throws IOException {
        var email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var result = uploadHandler.uploadFileHandler(file, email, description, category);
        if (result) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(true);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(false);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<Boolean> changePassword(@RequestBody User user) {
        var email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
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

    @GetMapping("/getAllMovies")
    public ResponseEntity<List<Movie>> getAllMovies() {
        var email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var userExist = userRepository.findByEmail(email);
        if (userExist != null) {
            var movies = movieRepository.findAllMoviesByUserId(userExist.getId());
            if (!movies.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(movies);
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(null);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    @PutMapping("/editMovie")
    public ResponseEntity<List<Movie>> editMovie(@RequestBody Movie movie) {
        var movieExist = movieRepository.findByMovieId(movie.getId());
        if (movieExist != null) {
            movieExist.setName(movie.getName());
            movieExist.setDescription(movie.getDescription());
            movieRepository.save(movieExist);

            var userExist = userRepository.findByEmail(SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal().toString());
            var movies = movieRepository.findAllMoviesByUserId(userExist.getId());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(movies);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    @PostMapping("/deleteMovie")
    public ResponseEntity<List<Movie>> deleteMovie(@RequestBody Movie movie) {
        var movieExist = movieRepository.findByMovieId(movie.getId());
        if (movieExist != null) {
            movieRepository.delete(movieExist);

            var email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            app42Service.removeFileByUser(movie.getName(), email);
            var userExist = userRepository.findByEmail(email);
            var movies = movieRepository.findAllMoviesByUserId(userExist.getId());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(movies);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    @PostMapping(value = "/addComment", produces = "application/json;charset=utf-8")
    public ResponseEntity<List<Comment>> addComment(@RequestParam("movieId") String movieId,
                                                    @RequestParam("content") String content,
                                                    @RequestParam("email") String email) throws UnsupportedEncodingException {
        var userExist = userRepository.findByEmail(email);
        content = new String(content.getBytes("ISO-8859-1"), "UTF-8");
        if (userExist != null) {
            commentRepository.insert(new Comment(userExist.getName(), movieId, content));
            var comments = commentRepository.findAllByMovieId(movieId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(comments);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }
}
