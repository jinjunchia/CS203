package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.EloRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EloRecordRepository extends JpaRepository<EloRecord, Long> {


}
