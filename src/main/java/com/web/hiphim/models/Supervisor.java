package com.web.hiphim.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document
@Data
@NoArgsConstructor
@Scope("prototype")
public class Supervisor {
    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed(unique = true)
    private String userEmail;
    @Indexed(expireAfterSeconds = 300)
    private Date createdTime = new Date();
    private String identifyCode = UUID.randomUUID().toString();
    private String password = "";

    public Supervisor(String userEmail) {
        this.userEmail = userEmail;
    }
}
