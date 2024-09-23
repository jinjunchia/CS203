package com.cs203.cs203system.service;

import com.cs203.cs203system.dtos.LoginResponse;
import com.cs203.cs203system.dtos.UserResponseMapper;
import com.cs203.cs203system.dtos.players.CreateAdminMapper;
import com.cs203.cs203system.dtos.players.CreateUserRequest;
import com.cs203.cs203system.dtos.players.CreatePlayerMapper;
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

@Service
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final AdminRepository adminRepository;

    private final PlayerRepository playerRepository;

    private final CreatePlayerMapper createPlayerMapper;

    private final CreateAdminMapper createAdminMapper;

    private final TokenService tokenService;
    private final UserResponseMapper userResponseMapper;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, AdminRepository adminRepository, PlayerRepository playerRepository,
                                 CreatePlayerMapper createPlayerMapper, CreateAdminMapper createAdminMapper, TokenService tokenService,
                                 UserResponseMapper userResponseMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.adminRepository = adminRepository;
        this.playerRepository = playerRepository;
        this.createPlayerMapper = createPlayerMapper;
        this.createAdminMapper = createAdminMapper;
        this.tokenService = tokenService;
        this.userResponseMapper = userResponseMapper;
    }

    public User register(CreateUserRequest createUserRequest) {
        createUserRequest.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        UserType userType = createUserRequest.getUserType();

        if (userType.equals(UserType.ROLE_PLAYER)) {
            Player newPlayer = createPlayerMapper.toEntity(createUserRequest);
            return playerRepository.save(newPlayer);
        }

        Admin newAdmin = createAdminMapper.toEntity(createUserRequest);
        return adminRepository.save(newAdmin);
    }

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

    public User getCurrentUser(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
