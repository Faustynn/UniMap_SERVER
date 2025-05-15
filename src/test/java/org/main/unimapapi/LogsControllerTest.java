package org.main.unimapapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.main.unimapapi.controllers.LogsController;
import org.main.unimapapi.dtos.LogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogsController.class)
@Import(LogsController.class)
@ContextConfiguration(classes = {LogsController.class, TestSecurityConfig.class})
class LogsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void receiveClientLog_returnsSuccessResponse() throws Exception {
        LogEntry entry = new LogEntry(1, "2025-05-15T16:40:00", "INFO", "This is a test log");

        mockMvc.perform(post("/api/unimap_pc/log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andExpect(status().isOk())
                .andExpect(content().string("Client log received successfully."));
    }

    @Test
    void receiveClientLog_invalidJson_returnsBadRequest() throws Exception {
        String invalidJson = "{\"type\": \"info\", \"message\": \"Missing closing brace\"";

        mockMvc.perform(post("/api/unimap_pc/log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
