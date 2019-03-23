package com.web.hiphim.controllers;

import com.web.hiphim.models.Comment;
import com.web.hiphim.models.Log;
import com.web.hiphim.models.Movie;
import com.web.hiphim.models.User;
import com.web.hiphim.repositories.ICommentRepository;
import com.web.hiphim.repositories.ILogRepository;
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
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("user")
@PreAuthorize("hasAnyRole('ROLE_USER')")
public class UserController {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private UploadHandler uploadHandler;
    @Autowired
    private ILogRepository logRepository;
    @Autowired
    private IMovieRepository movieRepository;
    @Autowired
    private ICommentRepository commentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private App42Service app42Service;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /*
    * Upload file
    * Return true if upload successfully
    * Otherwise return false
    * */
    @PostMapping("/uploadFile")
    public ResponseEntity<Boolean> uploadFile(@RequestParam("file") MultipartFile file,
                                              @RequestParam("description") String description,
                                              @RequestParam("category") String category) throws IOException {
        var email = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        description = new String(description.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        var result = uploadHandler.uploadFileHandler(file, email, description, category);
        if (result) {
            logRepository.insert(new Log(email, dateFormat.format(new Date()), "Uploaded new video"));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(true);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(false);
    }

    /*
    * Change password for user
    * Return true if change password successfully
    * Otherwise return false
    * */
    @PutMapping("/changePassword")
    public ResponseEntity<Boolean> changePassword(@RequestBody User user) {
        var email = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        var userExist = userRepository.findByEmail(email);
        if (userExist == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(false);
        }
        userExist.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userExist);
        logRepository.insert(new Log(email, dateFormat.format(new Date()), "Changed the password"));
        return ResponseEntity.status(HttpStatus.OK)
                .body(true);
    }

    /*
    * Get all movies by user
    * Return list of movies
    * Otherwise return null
    * */
    @GetMapping("/getAllMoviesByUser")
    public ResponseEntity<List<Movie>> getAllMovies() {
        var email = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
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

    /*
    * Edit movie
    * Returns the movie list if the movie has been edited
    * Otherwise return null
    * */
    @PutMapping("/editMovie")
    public ResponseEntity<List<Movie>> editMovie(@RequestBody Movie movie) {
        var movieExist = movieRepository.findByMovieId(movie.getId());
        if (movieExist != null) {
            movieExist.setName(movie.getName());
            movieExist.setDescription(movie.getDescription());
            movieRepository.save(movieExist);

            var email = userRepository.findByUserId(movie.getUserId()).getEmail();
            logRepository.insert(new Log(email, dateFormat.format(new Date()), "Edited the video"));

            var userExist = userRepository.findByEmail(((org.springframework.security.core.userdetails.User)
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
            var movies = movieRepository.findAllMoviesByUserId(userExist.getId());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(movies);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    /*
    * Delete movie by movieId
    * Returns the movie list if the movie has been deleted
    * Otherwise return null
    * */
    @PostMapping("/deleteMovie")
    public ResponseEntity<List<Movie>> deleteMovie(@RequestBody Movie movie) {
        var movieExist = movieRepository.findByMovieId(movie.getId());
        if (movieExist != null) {
            var nameMovie = movieExist.getName();
            movieRepository.delete(movieExist);

            var email = ((org.springframework.security.core.userdetails.User)
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            logRepository.insert(new Log(email, dateFormat.format(new Date()), "Deleted the video named " + nameMovie));
            app42Service.removeFileByUser(movie.getName(), email);
            var userExist = userRepository.findByEmail(email);
            var movies = movieRepository.findAllMoviesByUserId(userExist.getId());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(movies);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    /*
    * Add comment
    * Return the list of Comments
    * */
    @PostMapping(value = "/addComment", produces = "application/json;charset=utf-8")
    public ResponseEntity<List<Comment>> addComment(@RequestParam("movieId") String movieId,
                                                    @RequestParam("content") String content,
                                                    @RequestParam("email") String email) {
        var userExist = userRepository.findByEmail(email);
        content = new String(content.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        if (userExist != null) {
            commentRepository.insert(new Comment(userExist.getName(), movieId, content, dateFormat.format(new Date())));
            if (!userExist.getRoles().contains("admin")) {
                var movieName = movieRepository.findByMovieId(movieId).getName();
                logRepository.insert(new Log(email, dateFormat.format(new Date()), "Commented on the video named " + movieName));
            }
            var comments = commentRepository.findAllByMovieId(movieId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(comments);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }
}
