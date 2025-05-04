package org.main.unimapapi;

import org.junit.jupiter.api.Test;
import org.main.unimapapi.controllers.TeacherController;
import org.main.unimapapi.repository_queries.DataFatcherRepository;
import org.main.unimapapi.utils.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherController.class)
@WithMockUser(username = "nazar")
public class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtToken jwtToken;

    @MockBean
    private DataFatcherRepository dataFatcherRepository;

    @Test
    public void testGetAllTeachers_returnsOk() throws Exception {
        String token = "valid-token";

        // Mocking the behavior of JWT token validation and repository fetching teachers
        when(jwtToken.validateAccessToken(token)).thenReturn(true);
        when(dataFatcherRepository.fetchAllTeachers()).thenReturn(Collections.emptyList());

        // Performing the GET request and asserting the response
        mockMvc.perform(get("/api/unimap_pc/resources/teachers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())  // Expecting 200 OK status
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // Expecting JSON response
                .andExpect(jsonPath("$.teachers").isArray());  // Expecting the "teachers" field to be an array
    }

    @Test
    public void testGetAllTeachers_returnsUnauthorized_whenTokenInvalid() throws Exception {
        String token = "invalid-token";

        // Mocking invalid token response
        when(jwtToken.validateAccessToken(token)).thenReturn(false);

        // Performing the GET request and asserting the response
        mockMvc.perform(get("/api/unimap_pc/resources/teachers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())  // Expecting 401 Unauthorized status
                .andExpect(content().string(""));  // Expecting an empty response body (since the status is 401)
    }

    @Test
    public void testGetAllTeachers_returnsServerError_onException() throws Exception {
        String token = "valid-token";

        // Mocking valid token but simulating an exception in the repository
        when(jwtToken.validateAccessToken(token)).thenReturn(true);
        when(dataFatcherRepository.fetchAllTeachers()).thenThrow(new RuntimeException("DB error"));

        // Performing the GET request and asserting the response
        mockMvc.perform(get("/api/unimap_pc/resources/teachers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isInternalServerError())  // Expecting 500 Internal Server Error
                .andExpect(content().string(""));  // Expecting an empty response body (since the status is 500)
    }
}
