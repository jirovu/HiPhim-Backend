package com.web.hiphim.repositories;

import com.web.hiphim.models.Supervisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ISupervisorRepository extends MongoRepository<Supervisor, String> {
    @Value("{ identifyCode : ?0 }")
    Supervisor findByIdentifyCode(String identifyCode);

    @Value("{ userEmail : ?0 }")
    Supervisor findByUserEmail(String userEmail);
}
