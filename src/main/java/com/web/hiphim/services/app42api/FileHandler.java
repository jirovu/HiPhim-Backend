package com.web.hiphim.services.app42api;

import com.shephertz.app42.paas.sdk.java.upload.UploadFileType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

@Service
@Data
@NoArgsConstructor
public class FileHandler {
    // Path of the folder to store files
    private final String pathFiles = System.getProperty("user.dir") + "\\src\\main\\resources\\files";

    /*
     * Check exist file
     * Return True if file already exist
     * Otherwise return False
     * */
    public boolean checkFileExist(MultipartFile file) {
        var pathToStore = Paths.get(pathFiles + "\\" + file.getOriginalFilename());
        try {
            Files.getFileStore(pathToStore);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /*
     * Check exist file based on Path
     * Return True if file already exist
     * Otherwise return False
     * */
    private boolean checkFileExistByPath(Path path) {
        try {
            Files.getFileStore(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /*
     * Parse the file name
     * Return the HashMap contains type and name of file
     * */
    public HashMap<String, String> parseFileName(String originalFileName) {
        var typeOf = originalFileName.substring(originalFileName.indexOf("."));
        var nameOf = originalFileName.substring(0, originalFileName.indexOf("."));

        return new HashMap<>() {
            {
                put("typeOf", typeOf);
                put("nameOf", nameOf);
            }
        };
    }

    /*
     * Get file type
     * Return the file type to upload file
     * */
    public UploadFileType getFileType(String typeOf) {
        switch (typeOf) {
            case ".mp4":
                return UploadFileType.VIDEO;
            case ".jpg":
            case ".png":
                return UploadFileType.IMAGE;
            case ".mp3":
                return UploadFileType.AUDIO;
            default:
                return UploadFileType.TXT;
        }
    }

    /*
     * Get path and handle path to store file
     * Return Path to store file
     * */
    public Path getPath(MultipartFile file) {
        Path path = null;
        var resultOfFile = parseFileName(file.getOriginalFilename());

        if (resultOfFile != null) {
            // Handle file name
            var typeOf = resultOfFile.get("typeOf");
            var nameOf = resultOfFile.get("nameOf");

            for (int i = 2; i < 100; i++) {
                path = Paths.get(pathFiles + "\\" + nameOf + "part" + i + typeOf);
                // Check if file path is already exist - continue
                if (checkFileExistByPath(path)) {
                    continue;
                }
                // else return the path
                return path;
            }

            return path;
        } else {
            throw new AssertionError();
        }
    }
}
