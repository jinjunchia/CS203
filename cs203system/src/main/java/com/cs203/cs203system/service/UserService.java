package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.UserUpdateRequest;
import com.cs203.cs203system.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public interface UserService {
    User createUser(User user);

    Optional<User> findUserById(Integer id, UserUpdateRequest userUpdateRequest);

    List<User> findAllUsers();

//    Optional<User> findUserByEmailName(String email);
//
//    Optional<String> findUserEmail(String email);

    void deleteUserById(Integer id);
}
