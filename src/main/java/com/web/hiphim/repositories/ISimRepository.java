package com.web.hiphim.repositories;

import com.web.hiphim.models.Sim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ISimRepository extends JpaRepository<Sim, Integer> {
    @Modifying
    @Query(value = "SELECT sim.ans FROM sim WHERE sim.ask LIKE ?",
    nativeQuery = true)
    List<String> findByAsk(String ask);

    @Modifying
    @Query(value = "SELECT sim.ans FROM sim",
            nativeQuery = true)
    List<String> findAllAns();
}
