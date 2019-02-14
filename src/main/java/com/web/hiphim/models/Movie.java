package com.web.hiphim.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "movie")
@Data
@NoArgsConstructor
public class Movie {
    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed(unique = true)
    private String name;
    private String description;
    private String userId;
    private String url;
    private boolean approved;

    public Movie(String userId, String name, String description, String url, boolean approved) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.url = url;
        this.approved = approved;
    }
}
