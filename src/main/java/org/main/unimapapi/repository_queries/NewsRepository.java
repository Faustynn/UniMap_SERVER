package org.main.unimapapi.repository_queries;

import org.main.unimapapi.dtos.Coordinates_dto;
import org.main.unimapapi.dtos.News_dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class NewsRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public NewsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<News_dto> findByDateOfCreationAfter(LocalDateTime date) {
        String sql = "SELECT * FROM news WHERE date_of_creation > ?";
        return jdbcTemplate.query(sql, new NewsRowMapper(), date);
    }

    public List<News_dto> findAllNews() {
        String sql = "SELECT * FROM news";
        return jdbcTemplate.query(sql, new NewsRowMapper());
    }


    private static class NewsRowMapper implements RowMapper<News_dto> {
        @Override
        public News_dto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return News_dto.builder()
                    .id(rs.getInt("id"))
                    .title(rs.getString("title"))
                    .content(rs.getString("content"))
                    .coordinates(new Coordinates_dto(
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude")))
                    .date_of_creation(rs.getString("date_of_creation"))
                    .build();
        }
    }
}