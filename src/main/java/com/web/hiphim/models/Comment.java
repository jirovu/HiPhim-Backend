package com.web.hiphim.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "comment")
@CompoundIndex(def = "{'userId':1, 'movieId':1}", name = "userId_movieId_index")
@Data
@NoArgsConstructor
public class Comment {
    @Id
    private String id = UUID.randomUUID().toString();

    private String nameUser;
    private String movieId;
    private String content;
    private String timestamp;

    public Comment(String nameUser, String movieId, String content, String timestamp) {
        this.nameUser = nameUser;
        this.movieId = movieId;
        this.content = content;
        this.timestamp = timestamp;
    }
}
