package com.cs203.cs203system.service;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.cs203.cs203system.model.User;
import com.cs203.cs203system.repository.UserRepository;
import com.cs203.cs203system.service.impl.AuthenticationService;
import com.cs203.cs203system.service.impl.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testGenerateJwt_Success() {
        // Arrange: Mock Authentication and its methods
        Authentication auth = mock(Authentication.class);
        GrantedAuthority authority = mock(GrantedAuthority.class);

        // Set up mock behavior for Authentication and GrantedAuthority
        when(auth.getName()).thenReturn("testUser");
        when(auth.getAuthorities()).thenReturn((Collection) Collections.singletonList(authority));  // Use singletonList here
        when(authority.getAuthority()).thenReturn("ROLE_USER");

        // Mock the Jwt object which contains the token value
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mockedJwtToken");

        // Mock the JwtEncoder's encode method to return the mocked Jwt
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act: Call the method we're testing
        String token = tokenService.generateJwt(auth);

        // Assert: Check if the token is correct
        assertNotNull(token);
        assertEquals("mockedJwtToken", token);

        // Verify the interactions
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
        verify(auth, times(1)).getName();
        verify(auth, times(1)).getAuthorities();
    }



}
