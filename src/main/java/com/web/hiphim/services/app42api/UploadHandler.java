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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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
                                     String description, String category) {
        if (file.isEmpty())
            return false;
        long fileSize = file.getSize();
        if (fileSize > 1000000000)
            return false;

        Path path;
        byte[] arrByte;
        var resultOfFile = fileHandler.parseFileName(file.getOriginalFilename());
        var countArr = new ArrayList<Integer>();

        try (InputStream is = file.getInputStream()) {
            while (fileSize >= 500000000) {
                countArr.add(500000000);
                fileSize -= 500000000;
            }
            countArr.add((int) fileSize);

            if (fileHandler.checkFileExist(file))
                path = fileHandler.getPath(file);
            else
                path = Paths.get(fileHandler.getPathFiles() + "\\" + file.getOriginalFilename());
            OutputStream os = new FileOutputStream(path.toString());

            for (int i = 0; i < countArr.size(); i++) {
                arrByte = is.readNBytes(countArr.get(i));
                os.write(arrByte);
                if (i != countArr.size() - 1) {
                    os.flush();
                }
            }

            var movieName = path.getFileName().toString().substring(0, path.getFileName().toString().indexOf(".")).toString();
            app42Service.uploadFileForUser(username, movieName, description, path, fileHandler.getFileType(resultOfFile.get("typeOf")));

            var userExist = userRepository.findByEmail(username);
            var url = app42Service.getFileByUsername(username, movieName).get(0).getUrl();
            movieRepository.insert(new Movie(movieName, description, userExist.getId(), url, category, true));

            return true;

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            return false;
        }
    }
}
