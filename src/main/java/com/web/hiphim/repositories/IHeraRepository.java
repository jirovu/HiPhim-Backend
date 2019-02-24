package com.web.hiphim.repositories;

import com.web.hiphim.models.Hera;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface IHeraRepository extends MongoRepository<Hera, String> {
    @Query("{ 'ask' : { $regex: ?0 } }")
    List<Hera> findAnsByAsk(String ask);

    @Query("{}")
    List<Hera> findAllByLimit(Pageable pageable);
}
