package com.web.hiphim.services.app42api;

import com.web.hiphim.models.Movie;
import com.web.hiphim.repositories.IMovieRepository;
import com.web.hiphim.repositories.IUserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadHandler {
    @Autowired
    private App42Service app42Service;
    @Autowired
    private FileHandler fileHandler;
    @Autowired
    private IMovieRepository movieRepository;
    @Autowired
    private IUserRepository userRepository;

    /*
     * Upload file handler
     * Return True if success
     * Otherwise return False
     * */
    public boolean uploadFileHandler(MultipartFile file, String username,
                                     String description, String category) throws IOException {
        try {
            Path path = getPath(file);

            byte[] bytes = file.getBytes();
            Files.write(path, bytes);

            var movieName = path.getFileName().toString().substring(0, path.getFileName().toString().indexOf("."));
            var resultOfFile = fileHandler.parseFileName(file.getOriginalFilename());
            app42Service.uploadFileForUser(username, movieName, description, path, fileHandler.getFileType(resultOfFile.get("typeOf")));

            var userExist = userRepository.findByEmail(username);
            var url = app42Service.getFileByUsername(username, movieName).get(0).getUrl();
            movieRepository.insert(new Movie(movieName, description, userExist.getId(), url, category, false));

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private Path getPath(MultipartFile file) {
        if (fileHandler.checkFileExist(file))
            return fileHandler.getPath(file);
        else
            return Paths.get(fileHandler.getPathFiles() + "\\" + file.getOriginalFilename());
    }
}
