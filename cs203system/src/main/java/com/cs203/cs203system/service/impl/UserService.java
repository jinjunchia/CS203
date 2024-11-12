package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.model.User;
import com.cs203.cs203system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing user-related operations and user authentication.
 *
 * This service implements {@link UserDetailsService} to provide user
 * authentication functionality, including locating users by username
 * for Spring Security.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs a UserService with the necessary dependencies.
     *
     * @param userRepository the user repository for accessing user data
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id the unique ID of the user to retrieve
     * @return an {@link Optional} containing the user if found, or empty if not found
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Retrieves a list of all users.
     *
     * @return a list of all {@link User} entities
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may be case-sensitive or case-insensitive depending on the configuration.
     *
     * This method is used by Spring Security to authenticate users. If the user
     * is found, it returns a {@link UserDetails} object with the user's information.
     * If not found, it throws a {@link UsernameNotFoundException}.
     *
     * @param username the username identifying the user whose data is required
     * @return a fully populated user record (never {@code null})
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
    }
}

