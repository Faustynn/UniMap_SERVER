package org.main.unimapapi.controllers;

import lombok.RequiredArgsConstructor;
import org.main.unimapapi.dtos.Comment_dto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            comment.setDescription(rs.getString("description"));
            comment.setRating(rs.getInt("rating"));

            if(rs.getString("subject_code") != null) {
                comment.setLooking_id(rs.getString("subject_code"));
            }else{comment.setLooking_id(rs.getString("teacher_id"));}

            return comment;
        }
    };

    @GetMapping("/subject/{subject_id}")
    public ResponseEntity<List<Comment_dto>> getAllSubjectsComments() {
        try {
            String sql = "SELECT * FROM comments_subjects WHERE subject_code = ?";
            List<Comment_dto> subjectsList = jdbcTemplate.query(sql, subjectsRowMapper);
            return ResponseEntity.ok(subjectsList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/teacher/{teacher_id}")
    public ResponseEntity<List<Comment_dto>> getAllTeachersComments() {
        try {
            String sql = "SELECT * FROM comments_teachers WHERE teachers_id = ?";
            List<Comment_dto> teachersList = jdbcTemplate.query(sql, subjectsRowMapper);
            return ResponseEntity.ok(teachersList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
