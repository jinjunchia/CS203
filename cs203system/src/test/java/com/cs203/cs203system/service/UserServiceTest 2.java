//package com.cs203.cs203system.service;
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.cs203.cs203system.repository.UserRepository;
//import com.cs203.cs203system.service.impl.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import java.util.Optional;
//
//class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private UserService userService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testLoadUserByUsername_Success() {
//        // Arrange
//        String username = "existingUser";
//        UserDetails mockUser = User.withUsername(username).password("password").roles("USER").build();
//
//        // Mock repository to return the user
//        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(mockUser));
//
//
//        // Act
//        UserDetails result = userService.loadUserByUsername(username);
//
//        // Assert
//        assertNotNull(result);  // Ensure result is not null
//        assertEquals(username, result.getUsername());  // Ensure username matches
//        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);  // Verify repository was called once
//    }
//
//    @Test
//    void testLoadUserByUsername_UserNotFound() {
//        // Arrange
//        String username = "nonexistentUser";
//
//        // Mock repository to return Optional.empty()
//        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());
//
//        // Act & Assert: Expect UsernameNotFoundException to be thrown
//        assertThrows(UsernameNotFoundException.class, () -> {
//            userService.loadUserByUsername(username);
//        });
//
//        // Verify that the repository method was called once
//        verify(userRepository, times(1)).findByUsernameIgnoreCase(username);
//    }
//}
//
