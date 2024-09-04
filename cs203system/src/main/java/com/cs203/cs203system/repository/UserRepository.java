package com.cs203.cs203system.repository;

import com.cs203.cs203system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}