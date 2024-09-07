package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.Rules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RulesRepository extends JpaRepository<Rules, Integer> {
}