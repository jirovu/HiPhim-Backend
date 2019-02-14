package com.web.hiphim.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Document(collection = "log")
@Data
@NoArgsConstructor
public class Log {
    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed(unique = true)
    private String userId;
    private Date timestamp;
    private List<String> actions;

    public Log(String userId, Date timestamp, List<String> actions) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.actions = actions;
    }
}
