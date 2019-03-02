package com.web.hiphim.repositories;

import com.web.hiphim.models.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface IMovieRepository extends MongoRepository<Movie, String> {
    @Query(" { id : ?0, approved: true } ")
    Movie findByMovieId(String id);

    @Query(" { category : ?0, approved: true } ")
    List<Movie> findByCategory(String category);

    @Query(" { category: ?0, name: { $regex: ?1 }, approved: true } ")
    List<Movie> findByCategoryAndName(String category, String name);

    @Query(" { name: { $regex: ?0 } }, approved: true ")
    List<Movie> findByName(String name);

    @Query(" { userId : ?0 }, approved: true ")
    List<Movie> findAllMoviesByUserId(String userId);

    @Query(" {} ")
    List<Movie> findLimitMovies(Pageable pageable);
}
