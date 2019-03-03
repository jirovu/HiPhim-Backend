package com.web.hiphim.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Document(collection = "log")
@Data
@NoArgsConstructor
public class Log {
    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed
    private String userEmail;
    private String timestamp;
    private String action;

    public Log(String userEmail, String timestamp, String action) {
        this.userEmail = userEmail;
        this.timestamp = timestamp;
        this.action = action;
    }
}
