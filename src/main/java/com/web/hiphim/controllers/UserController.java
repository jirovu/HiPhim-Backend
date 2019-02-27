package com.web.hiphim.controllers;

import com.web.hiphim.models.Movie;
import com.web.hiphim.repositories.IUserRepository;
import com.web.hiphim.services.app42api.UploadHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("user")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private UploadHandler uploadHandler;

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
}
