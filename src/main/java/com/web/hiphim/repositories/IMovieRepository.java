package com.web.hiphim.repositories;

import com.web.hiphim.models.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public interface IMovieRepository extends MongoRepository<Movie, String> {
    @Query(" { id : ?0 } ")
    Movie findByMovieId(String id);
}
