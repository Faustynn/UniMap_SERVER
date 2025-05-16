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

/**
 * Service for managing news entries stored in the {@code news} table.
 *
 * <p>Provides methods for creating, updating, reading and deleting news.</p>
 */
@Service
@RequiredArgsConstructor
public class NewsService {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Retrieves all news records from the database, ordered by creation date descending.
     *
     * @return list of {@link News_dto} objects
     */
    public List<News_dto> getAllNews() {
        String sql = "SELECT * FROM news ORDER BY date_of_creation DESC";
        return jdbcTemplate.query(sql, new NewsRowMapper());
    }

    /**
     * Retrieves a specific news entry by its ID.
     *
     * @param id the ID of the news
     * @return {@link News_dto} or {@code null} if not found
     */
    public News_dto getNewsById(Long id) {
        String sql = "SELECT * FROM news WHERE id = ?";
        List<News_dto> results = jdbcTemplate.query(sql, new NewsRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Retrieves the latest {@code count} news entries.
     *
     * @param count the number of news items to return
     * @return list of {@link News_dto}
     */
    public List<News_dto> getLatestNews(int count) {
        String sql = "SELECT * FROM news ORDER BY date_of_creation DESC LIMIT ?";
        return jdbcTemplate.query(sql, new NewsRowMapper(), count);
    }

    /**
     * Saves or updates a news entry.
     *
     * @param news the news to save
     * @return the saved {@link News_dto} (without updated ID if it was inserted)
     */
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

    /**
     * Checks whether a news entry exists with the given ID.
     *
     * @param id the news ID to check
     * @return {@code true} if the entry exists, otherwise {@code false}
     */
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM news WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    /**
     * Deletes a news entry by its ID.
     *
     * @param id the news ID to delete
     */
    @Transactional
    public void deleteNewsById(Long id) {
        String sql = "DELETE FROM news WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    /**
     * Internal row mapper for converting SQL result sets into {@link News_dto} objects.
     */
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