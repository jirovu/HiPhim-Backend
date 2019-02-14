package com.web.hiphim.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "document")
@Data
@NoArgsConstructor
public class Comment {
    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed
    private String userId;
    @Indexed
    private String movieId;
    private String content;

    public Comment(String userId, String movieId, String content) {
        this.userId = userId;
        this.movieId = movieId;
        this.content = content;
    }
}
