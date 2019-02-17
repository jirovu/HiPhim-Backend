package com.web.hiphim.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.UUID;

@Document(collection = "user")
@Data
@NoArgsConstructor
public class User {
    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed(unique = true)
    @Email(regexp = "^(.+)@(.+)$")
    private String email;
    private String password;
    private String name;
    private String urlAvt;
    private List<String> roles;

    public User(String email, String password, String name, String urlAvt, List<String> roles) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.urlAvt = urlAvt;
        this.roles = roles;
    }
}
