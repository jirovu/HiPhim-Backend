package com.web.hiphim.repositories;

import com.web.hiphim.models.TokenBlacklist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ITokenBlacklist extends MongoRepository<TokenBlacklist, String> {
    @Query("{ token : ?0 }")
    ITokenBlacklist findByToken(String token);
}
