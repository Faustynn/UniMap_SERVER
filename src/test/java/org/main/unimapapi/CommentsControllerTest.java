package org.main.unimapapi;

import org.junit.jupiter.api.Test;
import org.main.unimapapi.controllers.CommentsController;
import org.main.unimapapi.repository_queries.CommentsRepository;
import org.main.unimapapi.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.dao.DataIntegrityViolationException;



import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentsController.class)
@WithMockUser(username = "nazar")
public class CommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentsRepository commentsRepository;

    @Test
    public void testGetAllSubjectsComments_returnsOk() throws Exception {
        when(commentsRepository.getAllSubjectsComments("MAT101"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/unimap_pc/comments/subject/MAT101"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetAllTeachersComments_returnOk() throws Exception {
        when(commentsRepository.getAllTeachersComments("0"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/unimap_pc/comments/teacher/0"))
                .andExpect((status().isOk()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }




}
