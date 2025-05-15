package org.main.unimapapi;

import org.junit.jupiter.api.Test;
import org.main.unimapapi.dtos.Teacher_dto;
import org.main.unimapapi.repository_queries.DataFatcherRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DataFatcherRepositoryTest {

    @Test
    void testFetchAllTeachers_handlesNullRolesGracefully() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("id")).thenReturn("T001");
        when(rs.getString("name")).thenReturn("John Doe");
        when(rs.getString("email")).thenReturn("john@fiit.stuba.sk");
        when(rs.getString("phone")).thenReturn("123456");
        when(rs.getString("office")).thenReturn("A-203");
        when(rs.getString("subject_code")).thenReturn(null);
        when(rs.getString("roles")).thenReturn(null);

        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        DataFatcherRepository repository = new DataFatcherRepository(jdbcTemplate);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenAnswer(invocation -> {
            RowMapper<Teacher_dto> mapper = invocation.getArgument(1);
            Teacher_dto mapped = mapper.mapRow(rs, 0);
            return List.of(mapped);
        });

        List<Teacher_dto> result = repository.fetchAllTeachers();

        assertTrue(result.get(0).getSubjects().isEmpty());
    }
}
