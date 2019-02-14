package com.web.hiphim.repositories;

import com.web.hiphim.models.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public interface MovieRepository extends MongoRepository<Movie, String> {
}
