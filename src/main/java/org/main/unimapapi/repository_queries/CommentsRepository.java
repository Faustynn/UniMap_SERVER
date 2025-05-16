package org.main.unimapapi.repository_queries;

import org.main.unimapapi.dtos.Comment_dto;
import org.main.unimapapi.services.TokenService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Repository class for managing subject and teacher comments in the database using JDBC.
 *
 * <p>Supports adding, retrieving, and deleting comments for both subjects and teachers.</p>
 */
@Repository
@RequiredArgsConstructor
public class CommentsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TokenService tokenService;

    /**
     * RowMapper for converting SQL ResultSet into {@link Comment_dto} instances.
     * Automatically maps `subject_code` or `teacher_id` to {@code looking_id}.
     */
    private final RowMapper<Comment_dto> commentsRowMapper = (rs, rowNum) -> {
        Comment_dto comment = new Comment_dto();
        comment.setUser_id(rs.getInt("user_id"));
        comment.setComment_id(rs.getInt("comment_id"));
        comment.setName(rs.getString("name"));
        comment.setDescription(rs.getString("description"));
        comment.setRating(rs.getString("rating"));
        comment.setLevelAccess(rs.getInt("levelaccess"));

        if (hasColumn(rs, "subject_code")) {
            comment.setLooking_id(rs.getString("subject_code"));
        } else if (hasColumn(rs, "teacher_id")) {
            comment.setLooking_id(rs.getString("teacher_id"));
        }

        return comment;
    };

    /**
     * Retrieves all comments associated with a specific subject.
     *
     * @param subjectId the subject's code
     * @return list of matching comments
     */
    public List<Comment_dto> getAllSubjectsComments(String subjectId) {
        String sql = "SELECT u_d.name, c_s.* FROM comments_subjects c_s INNER JOIN user_data u_d ON c_s.user_id = u_d.id WHERE c_s.subject_code = ?";
        return jdbcTemplate.query(sql, new Object[]{subjectId}, commentsRowMapper);
    }

    /**
     * Retrieves all comments associated with a specific teacher.
     *
     * @param teacherId the teacher's ID
     * @return list of matching comments
     */
    public List<Comment_dto> getAllTeachersComments(String teacherId) {
        String sql = "SELECT u_d.name, c_t.* FROM comments_teachers c_t INNER JOIN user_data u_d ON c_t.user_id = u_d.id WHERE c_t.teacher_id = ?";
        return jdbcTemplate.query(sql, new Object[]{teacherId}, commentsRowMapper);
    }

    /**
     * Adds a new comment related to a subject.
     */
    public void addNewSubjectComment(int userId, String subjectCode, String description, int rating, int levelAccess) {
        String sql = "INSERT INTO comments_subjects (user_id, subject_code, description, rating, levelaccess) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, subjectCode, description, rating, levelAccess);
    }

    /**
     * Adds a new comment related to a teacher.
     */
    public void addNewTeacherComment(int userId, String teacherId, String description, int rating, int levelAccess) {
        String sql = "INSERT INTO comments_teachers (user_id, teacher_id, description, rating, levelaccess) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, userId, teacherId, description, rating, levelAccess);
    }

    /**
     * Deletes a subject comment by its comment ID.
     */
    public void deleteSubjectComment(int commentId) {
        String sql = "DELETE FROM comments_subjects WHERE comment_id = ?";
        jdbcTemplate.update(sql, commentId);
    }

    /**
     * Deletes a teacher comment by its comment ID.
     */
    public void deleteTeacherComment(int commentId) {
        String sql = "DELETE FROM comments_teachers WHERE comment_id = ?";
        jdbcTemplate.update(sql, commentId);
    }

    /**
     * Utility method to check if a given column exists in the ResultSet.
     *
     * @param rs         the SQL result set
     * @param columnName the column to check
     * @return {@code true} if the column exists, otherwise {@code false}
     */
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}