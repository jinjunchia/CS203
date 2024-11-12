package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.dtos.players.*;
import com.cs203.cs203system.enums.UserType;
import com.cs203.cs203system.model.Admin;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.User;
import com.cs203.cs203system.repository.AdminRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user authentication and registration.
 * Provides methods for registering users, logging in, and retrieving the current user.
 */
@Service
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final AdminRepository adminRepository;

    private final PlayerRepository playerRepository;

    private final TokenService tokenService;

    private final UserResponseMapper userResponseMapper;

    /**
     * Constructs an AuthenticationService with the required dependencies.
     *
     * @param userRepository        the repository for accessing user data
     * @param passwordEncoder       the encoder for hashing passwords
     * @param authenticationManager the manager for authenticating users
     * @param adminRepository       the repository for accessing admin data
     * @param playerRepository      the repository for accessing player data
     * @param tokenService          the service for generating JWT tokens
     * @param userResponseMapper    the mapper for converting User entity to UserResponseDto
     */
    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager, AdminRepository adminRepository, PlayerRepository playerRepository,
                                 TokenService tokenService, UserResponseMapper userResponseMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.adminRepository = adminRepository;
        this.playerRepository = playerRepository;
        this.tokenService = tokenService;
        this.userResponseMapper = userResponseMapper;
    }

    /**
     * Registers a new user in the system.
     *
     * @param createUserRequest the request data containing user details for registration
     * @return the registered User entity
     */
    @Transactional
    public User register(CreateUserRequest createUserRequest) {
        if (userRepository.existsUserByUsernameIgnoreCase(createUserRequest.getUsername())
                || userRepository.existsUserByEmailIgnoreCase(createUserRequest.getEmail())) {
            throw new RuntimeException("Username or Email is already in use");
        }

        createUserRequest.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        UserType userType = createUserRequest.getUserType();

        if (userType.equals(UserType.ROLE_PLAYER)) {
            Player newPlayer = new Player();
            newPlayer.setUsername(createUserRequest.getUsername());
            newPlayer.setPassword(createUserRequest.getPassword());
            newPlayer.setName(createUserRequest.getName());
            newPlayer.setEmail(createUserRequest.getEmail());
            newPlayer.setUserType(UserType.ROLE_PLAYER);
            return playerRepository.save(newPlayer);
        }

        Admin newAdmin = new Admin();
        newAdmin.setUsername(createUserRequest.getUsername());
        newAdmin.setPassword(createUserRequest.getPassword());
        newAdmin.setEmail(createUserRequest.getEmail());
        newAdmin.setUserType(UserType.ROLE_ADMIN);
        return adminRepository.save(newAdmin);
    }

    /**
     * Authenticates a user and generates a JWT token if the credentials are valid.
     *
     * @param username the username of the user attempting to log in
     * @param password the password of the user attempting to log in
     * @return a LoginResponse containing the user details and JWT token if authentication is successful, or null if unsuccessful
     */
    @Transactional
    public LoginResponse loginUser(String username, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = tokenService.generateJwt(auth);

            return new LoginResponse(userResponseMapper
                    .toDto(userRepository.findByUsernameIgnoreCase(username).get()), token);

        } catch (AuthenticationException e) {
            return new LoginResponse(null, "");
        }
    }

    /**
     * Retrieves the currently authenticated user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the User entity corresponding to the provided username
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    public User getCurrentUser(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
