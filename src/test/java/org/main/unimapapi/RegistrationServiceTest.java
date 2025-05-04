package org.main.unimapapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.unimapapi.dtos.User_dto;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.UserRepository;
import org.main.unimapapi.services.RegistrationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_validUser_savesSuccessfully() {
        User_dto dto = new User_dto();
        dto.setUsername("Adam");
        dto.setEmail("adam@mail.com");
        dto.setLogin("coolAdam");
        dto.setPassword("strongPassword");
        dto.setAdmin(false);
        dto.setPremium(true);
        dto.setAvatarBinary(Base64.getEncoder().encodeToString("avatar-data".getBytes()).getBytes());
        dto.setAvatarFileName("avatar.png");

        when(userRepository.save(any(User.class))).thenReturn(true);

        User result = registrationService.register(dto);

        assertNotNull(result);
        assertEquals("Adam", result.getUsername());
        assertEquals("adam@mail.com", result.getEmail());
        assertEquals("coolAdam", result.getLogin());
        assertTrue(result.isPremium());
        assertEquals("avatar.png", result.getAvatarFileName());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_nullAvatar_doesNotThrow() {
        User_dto dto = new User_dto();
        dto.setUsername("Emma");
        dto.setEmail("emma@mail.com");
        dto.setLogin("emma");
        dto.setPassword("securePassword");
        dto.setAvatarBinary(null);
        dto.setAvatarFileName(null);

        when(userRepository.save(any(User.class))).thenReturn(true);

        User result = registrationService.register(dto);

        assertNotNull(result);
        assertNull(result.getAvatar());
        assertNull(result.getAvatarFileName());
    }

    @Test
    void testRegister_duplicateLogin_throwsException() {
        User_dto dto = new User_dto();
        dto.setLogin("duplicate");
        dto.setPassword("pwd");
        dto.setEmail("dupe@example.com");
        dto.setUsername("Dupe");

        when(userRepository.findByLogin("duplicate")).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> registrationService.register(dto));
        assertTrue(exception.getMessage().contains("Error during user registration"));
    }
}
