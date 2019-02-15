package com.web.hiphim.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document(collection = "user")
@Data
@NoArgsConstructor
public class User {
    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed(unique = true)
    private String username;
    private String password;
    private String name;
    private String urlAvt;
    private List<String> roles;

    public User(String username, String password, String name, String urlAvt, List<String> roles) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.urlAvt = urlAvt;
        this.roles = roles;
    }
}
