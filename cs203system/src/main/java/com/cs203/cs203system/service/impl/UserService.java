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
 * Service class for managing user-related operations.
 * Implements the {@link UserDetailsService} interface to integrate with Spring Security.
 */
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Constructs a new {@code UserService} with the provided {@link UserRepository}.
     *
     * @param userRepository the repository used for user data access operations.
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the ID of the user to be retrieved.
     * @return an {@link Optional} containing the user if found, or an empty {@code Optional} if not found.
     */
    public Optional<User> getUserById(Long id) {
        return userRepository
                .findById(id);
    }

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a {@link List} of {@link User} objects representing all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserDetails(Long id, User updatedUserDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEmail(updatedUserDetails.getEmail());

        return userRepository.save(updatedUserDetails);
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may be case-sensitive, or case-insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested.
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()-> new UsernameNotFoundException("User '" + username + "' not found"));
    }
}
