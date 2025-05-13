package org.main.unimapapi.controllers;

import org.main.unimapapi.repository_queries.CommentsRepository;
import org.main.unimapapi.dtos.Comment_dto;
import org.main.unimapapi.services.TokenService;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * Controller for managing comments on subjects and teachers
 *
 * URL prefix: /api/unimap_pc/comments
 */
@RestController
@RequestMapping("/api/unimap_pc/comments")
public class CommentsController {

    private final CommentsRepository commentsRepository;
    private final TokenService tokenService;

    @Autowired
    public CommentsController(CommentsRepository commentsRepository, TokenService tokenService) {
        this.commentsRepository = commentsRepository;
        this.tokenService = tokenService;
    }

    /*
     * Method: GET
     * Endpoint: /subject/{subject_id}
     */
    @GetMapping("/subject/{subject_id}")
    public ResponseEntity<List<Comment_dto>> getAllSubjectsComments(@PathVariable("subject_id") String subjectId) {
        try {
            List<Comment_dto> subjectsList = commentsRepository.getAllSubjectsComments(subjectId);
            return ResponseEntity.ok(subjectsList);
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error fetching comments for subject: " + subjectId);
           // e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * Method: GET
     * Endpoint: /teacher/{teacher_id}
     */
    @GetMapping("/teacher/{teacher_id}")
    public ResponseEntity<List<Comment_dto>> getAllTeachersComments(@PathVariable("teacher_id") String teacherId) {
        try {
            List<Comment_dto> teachersList = commentsRepository.getAllTeachersComments(teacherId);
            return ResponseEntity.ok(teachersList);
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error fetching comments for teacher: " + teacherId);
        //    e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    /*
     * Method: POST
     * Endpoint: /subject
     * Authorisation header required (JWT access token)
     */
    @PostMapping("/subject")
    public ResponseEntity<Void> addNewSubjectComment(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String accessToken) {
        try {
            String token = accessToken.replace("Bearer ", "");
            String username = tokenService.getLoginFromAccessToken(token);
            if (!tokenService.validateAccessToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

      //      System.out.println("TEEEEEEEEST " + payload);

            int userId = (payload.get("user_id") instanceof Integer)
                    ? (Integer) payload.get("user_id")
                    : Integer.parseInt((String) payload.get("user_id"));
            String subjectCode = (String) payload.get("code");
            String description = (String) payload.get("text");
            int rating = (payload.get("rating") instanceof Integer)
                    ? (Integer) payload.get("rating")
                    : Integer.parseInt((String) payload.get("rating"));
            int levelAccess = (payload.get("levelAccess") instanceof Integer)
                    ? (Integer) payload.get("levelAccess")
                    : Integer.parseInt((String) payload.get("levelAccess"));

            commentsRepository.addNewSubjectComment(userId, subjectCode, description, rating, levelAccess);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error adding new subject comment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /*
     * Method: POST
     * Endpoint: /teacher
     * Authorisation header required (JWT access token)
     */
    @PostMapping("/teacher")
    public ResponseEntity<Void> addNewTeacherComment(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String accessToken) {
        try {
            String token = accessToken.replace("Bearer ", "");
            String username = tokenService.getLoginFromAccessToken(token);
            if (!tokenService.validateAccessToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            System.out.println("jeloooo  "+payload);

            int userId = (payload.get("user_id") instanceof Integer)
                    ? (Integer) payload.get("user_id")
                    : Integer.parseInt((String) payload.get("user_id"));
            String teacherId = (String) payload.get("code");
            String description = (String) payload.get("text");
            int rating = (payload.get("rating") instanceof Integer)
                    ? (Integer) payload.get("rating")
                    : Integer.parseInt((String) payload.get("rating"));
            int levelAccess = (payload.get("levelAccess") instanceof Integer)
                    ? (Integer) payload.get("levelAccess")
                    : Integer.parseInt((String) payload.get("levelAccess"));

            commentsRepository.addNewTeacherComment(userId, teacherId, description, rating, levelAccess);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error adding new teacher comment: " + e.getMessage());
          //  e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * Method: DELETE
     * Endpoint: /subject/{comment_id}
     */
    @DeleteMapping("/subject/{comment_id}")
    public ResponseEntity<Void> deleteSubjectComment(@PathVariable("comment_id") int commentId) {
        try {
            commentsRepository.deleteSubjectComment(commentId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error deleting subject comment: " + e.getMessage());
          //  e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
     * Method: DELETE
     * Endpoint: /teacher/{comment_id}
     */
    @DeleteMapping("/teacher/{comment_id}")
    public ResponseEntity<Void> deleteTeacherComment(@PathVariable("comment_id") int commentId) {
        try {
            commentsRepository.deleteTeacherComment(commentId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error deleting teacher comment: " + e.getMessage());
        //    e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}



