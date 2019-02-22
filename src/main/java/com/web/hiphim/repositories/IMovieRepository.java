package com.web.hiphim.repositories;

import com.web.hiphim.models.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface IMovieRepository extends MongoRepository<Movie, String> {
    @Query(" { id : ?0 } ")
    Movie findByMovieId(String id);

    @Query(" { category : ?0 } ")
    List<Movie> findByCategory(String category);
}
