package com.cs203.cs203system.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.cs203.cs203system.dtos.UserResponseDto;
import com.cs203.cs203system.dtos.players.*;
import com.cs203.cs203system.enums.UserType;
import com.cs203.cs203system.model.Admin;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.User;
import com.cs203.cs203system.repository.AdminRepository;
import com.cs203.cs203system.repository.PlayerRepository;
import com.cs203.cs203system.repository.UserRepository;
import com.cs203.cs203system.service.impl.AuthenticationService;
import com.cs203.cs203system.service.impl.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CreatePlayerMapper createPlayerMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CreateAdminMapper createAdminMapper;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserResponseMapper userResponseMapper;

    @InjectMocks
    private AuthenticationService authenticationService;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testRegister_PlayerSuccess() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("playerUser");
        request.setPassword("password123");
        request.setUserType(UserType.ROLE_PLAYER);

        Player player = new Player();
        when(createPlayerMapper.toEntity(request)).thenReturn(player);
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        User result = authenticationService.register(request);

        // Assert
        assertEquals(player, result); // Ensure the player is returned
        verify(passwordEncoder, times(1)).encode("password123");
        verify(playerRepository, times(1)).save(any(Player.class));
        verify(adminRepository, times(0)).save(any(Admin.class)); // Ensure admin save is never called
    }

    @Test
    void testRegister_AdminSuccess() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("adminUser");
        request.setPassword("password123");
        request.setUserType(UserType.ROLE_ADMIN);

        Admin admin = new Admin();
        when(createAdminMapper.toEntity(request)).thenReturn(admin);
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        User result = authenticationService.register(request);

        // Assert
        assertEquals(admin, result); // Ensure the admin is returned
        verify(passwordEncoder, times(1)).encode("password123");
        verify(adminRepository, times(1)).save(any(Admin.class));
        verify(playerRepository, times(0)).save(any(Player.class)); // Ensure player save is never called
    }

    @Test
    void testLoginUser_Success() {
        // Arrange
        String username = "testUser";
        String password = "testPassword";
        User user = new User();
        user.setUsername(username);

        Authentication auth = mock(Authentication.class);

        // Mock the authentication process
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(tokenService.generateJwt(auth)).thenReturn("mockedToken");
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(user));
        when(userResponseMapper.toDto(user)).thenReturn(new UserResponseDto(user.getId(),user.getUsername(), user.getEmail(), user.getUserType()));

        // Act
        LoginResponse response = authenticationService.loginUser(username, password);

        // Assert
        assertNotNull(response);
        assertEquals("mockedToken", response.getJwt());
        assertNotNull(response.getUser());
        assertEquals(username, response.getUser().getUsername());

        // Verify that the right methods were called
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(1)).generateJwt(auth);
        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);
        verify(userResponseMapper, times(1)).toDto(user);
    }

    @Test
    void testLoginUser_Failure() {
        // Arrange
        String username = "testUser";
        String password = "testPassword";

        // Simulate authentication failure by throwing BadCredentialsException
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act
        LoginResponse response = authenticationService.loginUser(username, password);

        // Assert
        assertNotNull(response);
        assertNull(response.getUser());  // User should be null on failure
        assertEquals("", response.getJwt());  // Token should be empty on failure

        // Verify that the right methods were called
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(0)).generateJwt(any());
        verify(userRepository, times(0)).findByUsernameIgnoreCase(anyString());
        verify(userResponseMapper, times(0)).toDto(any());
    }

    @Test
    void testGetCurrentUser_Success() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        // Mock the repository to return the user
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(user));

        // Act
        User result = authenticationService.getCurrentUser(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void testGetCurrentUser_Fail() {
        // Arrange
        String username = "nonexistentuser";

        // Mock the repository to return the user
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

        // Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.getCurrentUser(username);
        });

        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);
    }


}

