package com.web.hiphim.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document(collection = "token-blacklist")
@Data
@NoArgsConstructor
public class TokenBlacklist {
    @Id
    private String id = UUID.randomUUID().toString();

    private String token;
    @Indexed(expireAfterSeconds = 86400)
    private Date createdTime = new Date();

    public TokenBlacklist(String token) {
        this.token = token;
    }
}
