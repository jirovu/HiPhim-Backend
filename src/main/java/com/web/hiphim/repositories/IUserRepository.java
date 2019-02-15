package com.web.hiphim.repositories;

import com.web.hiphim.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public interface IUserRepository extends MongoRepository<User, String> {
    @Query("{ username : ?0 }")
    User findByUsername(String username);
}