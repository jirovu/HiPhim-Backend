package com.web.hiphim.repositories;

import com.web.hiphim.models.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public interface CommentRepository extends MongoRepository<Comment, String> {
}
