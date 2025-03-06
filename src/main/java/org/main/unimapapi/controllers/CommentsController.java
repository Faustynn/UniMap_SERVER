package org.main.unimapapi.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import lombok.RequiredArgsConstructor;
import org.main.unimapapi.dtos.Comment_dto;
import org.main.unimapapi.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;
import org.main.unimapapi.services.TokenService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/unimap_pc/comments")
public class CommentsController {
    private final JdbcTemplate jdbcTemplate;
    private final TokenService tokenService;

    private boolean isNum(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return id.matches("\\d+");
    }

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
    public ResponseEntity<List<Comment_dto>> getAllSubjectsComments(@PathVariable("subject_id") String subjectId) {
        try {
            String sql = "SELECT u_d.name, c_s.* FROM comments_subjects c_s INNER JOIN user_data u_d ON c_s.user_id = u_d.id WHERE c_s.subject_code = ?";
//            if (!isNum(subjectId)) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
//            }
            List<Comment_dto> subjectsList = jdbcTemplate.query(sql, new Object[]{subjectId}, subjectsRowMapper);
            return ResponseEntity.ok(subjectsList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/teacher/{teacher_id}")
    public ResponseEntity<List<Comment_dto>> getAllTeachersComments(@PathVariable("teacher_id") String teacherId) {
        try {
            String sql = "SELECT u_d.name, c_t.* FROM comments_teachers c_t INNER JOIN user_data u_d ON c_t.user_id = u_d.id WHERE c_t.teacher_id = ?";
//            if (!isNum(teacherId)) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
//            }
            List<Comment_dto> teachersList = jdbcTemplate.query(sql, new Object[]{teacherId}, subjectsRowMapper);
            return ResponseEntity.ok(teachersList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @PostMapping("/subject")
    public ResponseEntity<Void> addNewSubjectComment(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String accessToken) {
        try {
            String token = accessToken.replace("Bearer ", "");
            String username = tokenService.getLoginFromAccessToken(token);
            if (!tokenService.validateAccessToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            int userId = Integer.parseInt((String) payload.get("user_id"));
            String subjectCode = (String) payload.get("code");
            String description = (String) payload.get("text");
            int rating = (int) payload.get("rating");
            int levelAccess = Integer.parseInt((String) payload.get("levelAccess"));

            String sql = "INSERT INTO comments_subjects (user_id, subject_code, description, rating, levelaccess) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, userId, subjectCode, description, rating, levelAccess);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/teacher")
    public ResponseEntity<Void> addNewTeacherComment(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String accessToken) {
        try {
            String token = accessToken.replace("Bearer ", "");
            String username = tokenService.getLoginFromAccessToken(token);
            if (!tokenService.validateAccessToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            int userId = Integer.parseInt((String) payload.get("user_id"));
            String teacherId = (String) payload.get("code");
            String description = (String) payload.get("text");
            int rating = (int) payload.get("rating");
            int levelAccess = Integer.parseInt((String) payload.get("levelAccess"));

            String sql = "INSERT INTO comments_teachers (user_id, teacher_id, description, rating, levelaccess) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, userId, teacherId, description, rating, levelAccess);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/subject/{comment_id}")
    public ResponseEntity<Void> deleteSubjectComment(@PathVariable("comment_id") int commentId) {
        try {
            String sql = "DELETE FROM comments_subjects WHERE comment_id = ?";
            jdbcTemplate.update(sql, commentId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/teacher/{comment_id}")
    public ResponseEntity<Void> deleteTeacherComment(@PathVariable("comment_id") int commentId) {
        try {
            String sql = "DELETE FROM comments_teachers WHERE comment_id = ?";
            jdbcTemplate.update(sql, commentId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}