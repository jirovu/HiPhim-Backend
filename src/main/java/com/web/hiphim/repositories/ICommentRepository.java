package com.web.hiphim.repositories;

import com.web.hiphim.models.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface ICommentRepository extends MongoRepository<Comment, String> {
    @Query(" { movieId: ?0 } ")
    List<Comment> findAllByMovieId(String movieId);
}
