package com.web.hiphim.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "token-blacklist")
@Data
@NoArgsConstructor
public class TokenBlacklist {
    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed(unique = true, expireAfterSeconds = 86400)
    private String token;

    public TokenBlacklist(String token) {
        this.token = token;
    }
}
