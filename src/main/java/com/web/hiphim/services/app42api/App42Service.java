package com.web.hiphim.services.app42api;

import com.shephertz.app42.paas.sdk.java.ACL;
import com.shephertz.app42.paas.sdk.java.App42API;
import com.shephertz.app42.paas.sdk.java.upload.Upload;
import com.shephertz.app42.paas.sdk.java.upload.UploadFileType;
import com.shephertz.app42.paas.sdk.java.upload.UploadService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

/*
 * This class used to connect and config App42 API
 * Purpose to use functions of App42 API such as (Upload file, Remove file, etc .. )
 * */
@Service
@Data
@AllArgsConstructor
public class App42Service {
    @Value("${spring.app42.apiKey}")
    private String apiKey;
    @Value("${spring.app42.secretKey}")
    private String secretKey;
    private UploadService uploadService;
    private HashSet<ACL> aclList;

    public App42Service() {
        App42API.initialize(apiKey, secretKey);
        uploadService = App42API.buildUploadService();
        aclList = new HashSet<>() {
            {
                add(new ACL("admin", ACL.Permission.READ));
                add(new ACL("admin", ACL.Permission.WRITE));
            }
        };
    }

    /*
     * Upload file for User and assign ROLE permissions to ADMIN to manage that file
     * Return True if success
     * Otherwise return False
     * */
    public boolean uploadFileForUser(String username, String fileName, String description, Path path,
                                     UploadFileType fileType) {
        try {
            uploadService.uploadFileForUser(fileName, username, path.toString(), fileType, description);
            // Grant role to admin
            uploadService.grantAccess(fileName, "admin", aclList);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /*
     * Get all files
     * Return list contains all files
     * */
    public List<Upload.File> getAllFiles() {
        return uploadService.getAllFiles().getFileList();
    }

    /*
     * Get all files for User
     * Return list files of user by username
     * */
    public List<Upload.File> getAllFilesForUser(String username) {
        return uploadService.getAllFilesByUser(username).getFileList();
    }

    /*
     * Remove file by User
     * Return True if success
     * Otherwise return False
     * */
    public boolean removeFileByName(String fileName, String username) {
        try {
            uploadService.removeFileByUser(fileName, username);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /*
     * Remove all files by User
     * Return True if success
     * Otherwise return False
     * */
    public boolean removeAllFilesByUser(String username) {
        try {
            uploadService.removeAllFilesByUser(username);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
