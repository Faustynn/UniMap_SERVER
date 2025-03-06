package org.main.unimapapi.controllers;

import lombok.RequiredArgsConstructor;
import org.main.unimapapi.dtos.Comment_dto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/unimap_pc/comments")
public class CommentsController {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Comment_dto> subjectsRowMapper = new RowMapper<Comment_dto>() {
        @Override
        public Comment_dto mapRow(ResultSet rs, int rowNum) throws SQLException {
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
        }
    };
    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    @GetMapping("/subject/{subject_id}")
    public ResponseEntity<List<Comment_dto>> getAllSubjectsComments(@PathVariable String subject_id) {
        try {
            String sql = "SELECT u_d.name, c_s.*\n" +
                    "FROM comments_subjects c_s\n" +
                    "    INNER JOIN user_data u_d ON c_s.user_id = u_d.id\n" +
                    "WHERE c_s.subject_code = ?";
            List<Comment_dto> subjectsList = jdbcTemplate.query(sql, new Object[]{subject_id}, subjectsRowMapper);
            return ResponseEntity.ok(subjectsList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/teacher/{teacher_id}")
    public ResponseEntity<List<Comment_dto>> getAllTeachersComments(@PathVariable String teacher_id) {
        try {
            String sql = "SELECT u_d.name, c_t.*\n" +
                    "FROM comments_teachers c_t\n" +
                    "    INNER JOIN user_data u_d ON c_t.user_id = u_d.id\n" +
                    "WHERE c_t.teacher_id = ?";
            List<Comment_dto> teachersList = jdbcTemplate.query(sql, new Object[]{teacher_id}, subjectsRowMapper);
            return ResponseEntity.ok(teachersList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/subject")
    public ResponseEntity<Void> addNewSubjectComment(@RequestBody Comment_dto comment) {
        try {
            String sql = "INSERT INTO comments_subjects (user_id, subject_code, description, rating, levelaccess) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, comment.getUser_id(), comment.getLooking_id(), comment.getDescription(), comment.getRating(), comment.getLevelAccess());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/teacher")
    public ResponseEntity<Void> addNewTeacherComment(@RequestBody Comment_dto comment) {
        try {
            String sql = "INSERT INTO comments_teachers (user_id, teacher_id, description, rating, levelaccess) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, comment.getUser_id(), comment.getLooking_id(), comment.getDescription(), comment.getRating(), comment.getLevelAccess());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @DeleteMapping("/subject/{comment_id}")
    public ResponseEntity<Void> deleteSubjectComment(@PathVariable int comment_id) {
        try {
            String sql = "DELETE FROM comments_subjects WHERE comment_id = ?";
            jdbcTemplate.update(sql, comment_id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/teacher/{comment_id}")
    public ResponseEntity<Void> deleteTeacherComment(@PathVariable int comment_id) {
        try {
            String sql = "DELETE FROM comments_teachers WHERE comment_id = ?";
            jdbcTemplate.update(sql, comment_id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}