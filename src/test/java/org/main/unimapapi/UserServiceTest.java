package org.main.unimapapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.main.unimapapi.entities.User;
import org.main.unimapapi.repository_queries.UserRepository;
import org.main.unimapapi.services.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "nazar")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testChangeEmail_userExists_emailUpdated() {
        User user = new User();
        user.setEmail("old@example.com");

        when(userRepository.findByLogin("test")).thenReturn(Optional.of(user));

        boolean result = userService.changeEmail("test", "new@example.com");

        assertTrue(result);
        verify(userRepository).update(user);
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    public void testDelete_userId_callsDeleteById() {
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }
}
