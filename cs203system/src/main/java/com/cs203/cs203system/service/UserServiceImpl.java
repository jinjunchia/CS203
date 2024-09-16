package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.UserUpdateRequest;
import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.model.User;
import com.cs203.cs203system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) { return userRepository.save(user);}

    @Override
    public Optional<User> findUserById(Integer id, UserUpdateRequest userUpdateRequest) {
        Optional<User> existingUser = userRepository.findUserById(id);
        if (existingUser.isEmpty()) {
            throw new NotFoundException("User " + id + " does not exist");
        }

        User user = existingUser.get();
        return userRepository.findUserById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUserById(Integer id) {
        Optional<User> existingUser = userRepository.findUserById(id);
        if (existingUser.isEmpty()) {
            throw new NotFoundException("User " + id + " does not exist");
        }
        userRepository.deleteById(id);
    }
}
