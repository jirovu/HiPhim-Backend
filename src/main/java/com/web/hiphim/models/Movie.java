package com.web.hiphim.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
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
    private String category;
    private boolean approved;

    public Movie(String name, String description, String userId, String url, String category, boolean approved) {
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.url = url;
        this.category = category;
        this.approved = approved;
    }
}
