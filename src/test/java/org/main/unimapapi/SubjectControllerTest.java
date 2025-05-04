package org.main.unimapapi;

import org.junit.jupiter.api.Test;
import org.main.unimapapi.controllers.SubjectController;
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

@WebMvcTest(SubjectController.class)
@WithMockUser(username = "nazar")
public class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtToken jwtToken;

    @MockBean
    private DataFatcherRepository dataFatcherRepository;

    @Test
    public void testGetAllSubjects_returnsOk() throws Exception {
        String token = "valid-token";

        when(jwtToken.validateAccessToken(token)).thenReturn(true);
        when(dataFatcherRepository.fetchAllSubjects()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/unimap_pc/resources/subjects")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subjects").isArray());
    }

    @Test
    public void testGetAllSubjects_returnsUnauthorized_whenTokenInvalid() throws Exception {
        String token = "invalid-token";

        when(jwtToken.validateAccessToken(token)).thenReturn(false);

        mockMvc.perform(get("/api/unimap_pc/resources/subjects")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    public void testGetAllSubjects_returnsServerError_onException() throws Exception {
        String token = "valid-token";

        when(jwtToken.validateAccessToken(token)).thenReturn(true);
        when(dataFatcherRepository.fetchAllSubjects()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/unimap_pc/resources/subjects")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }
}
