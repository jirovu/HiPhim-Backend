package com.web.hiphim.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "hera")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hera {
    @Id
    private String id = UUID.randomUUID().toString();

    @Indexed
    private String ask;
    private String ans;

    public Hera(String ask, String ans) {
        this.ask = ask;
        this.ans = ans;
    }
}
