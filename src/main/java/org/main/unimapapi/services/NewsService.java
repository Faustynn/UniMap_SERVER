package org.main.unimapapi.services;

import lombok.RequiredArgsConstructor;
import org.main.unimapapi.dtos.News_dto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final JdbcTemplate jdbcTemplate;

    public List<News_dto> getAllNews() {
        String sql = "SELECT * FROM news ORDER BY date_of_creation DESC";
        return jdbcTemplate.query(sql, new NewsRowMapper());
    }

    public News_dto getNewsById(Long id) {
        String sql = "SELECT * FROM news WHERE id = ?";
        List<News_dto> results = jdbcTemplate.query(sql, new NewsRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<News_dto> getLatestNews(int count) {
        String sql = "SELECT * FROM news ORDER BY date_of_creation DESC LIMIT ?";
        return jdbcTemplate.query(sql, new NewsRowMapper(), count);
    }

    @Transactional
    public News_dto saveNews(News_dto news) {
        if (news.getId() > 0) {
            String updateSql = "UPDATE news SET title = ?, content = ?, date_of_creation = ? WHERE id = ?";
            jdbcTemplate.update(updateSql, news.getTitle(), news.getContent(), news.getDate_of_creation(), news.getId());
        } else {
            String insertSql = "INSERT INTO news (title, content, date_of_creation) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertSql, news.getTitle(), news.getContent(), news.getDate_of_creation());
        }
        return news;
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM news WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Transactional
    public void deleteNewsById(Long id) {
        String sql = "DELETE FROM news WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class NewsRowMapper implements RowMapper<News_dto> {
        @Override
        public News_dto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return News_dto.builder()
                    .id(rs.getInt("id"))
                    .title(rs.getString("title"))
                    .content(rs.getString("content"))
                    .date_of_creation(rs.getString("date_of_creation"))
                    .build();
        }
    }
}