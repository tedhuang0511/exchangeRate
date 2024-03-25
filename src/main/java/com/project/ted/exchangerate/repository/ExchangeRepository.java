package com.project.ted.exchangerate.repository;

import com.project.ted.exchangerate.entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Integer> {
    @Query("select e from Exchange e where e.createDate = (select e.createDate from Exchange e order by e.createDate desc limit 1)")
    List<Exchange> findLatestExchanges();

    @Query("select e.createDate from Exchange e order by e.createDate desc limit 1")
    Date getLastUpdateTime();
}
