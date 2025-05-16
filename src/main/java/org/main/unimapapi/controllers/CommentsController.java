package org.main.unimapapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

/**
 * REST controller for managing comments on subjects and teachers.
 *
 * <p><strong>Base URL:</strong> <code>/api/unimap_pc/comments</code></p>
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

    /**
     * Retrieves all comments associated with a specific subject.
     *
     * @param subjectId the subject code
     * @return list of {@link Comment_dto} objects
     */
    @Operation(summary = "Get subject comments", description = "Returns all comments for a given subject.")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
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

    /**
     * Retrieves all comments associated with a specific teacher.
     *
     * @param teacherId the teacher's identifier
     * @return list of {@link Comment_dto} objects
     */
    @Operation(summary = "Get teacher comments", description = "Returns all comments for a given teacher.")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
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

    /**
     * Adds a new comment to a subject.
     *
     * @param payload request body containing user_id, code, text, rating, and levelAccess
     * @param accessToken JWT access token for authorization
     * @return 201 Created or appropriate error response
     */
    @Operation(summary = "Add subject comment", description = "Adds a new comment for a subject. Requires authentication.")
    @ApiResponse(responseCode = "201", description = "Comment added successfully")
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

            commentsRepository.addNewSubjectComment(userId, subjectCode, description, rating, levelAccess);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error adding new subject comment: " + e.getMessage());
          //  e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Adds a new comment to a teacher.
     *
     * @param payload request body containing user_id, code, text, rating, and levelAccess
     * @param accessToken JWT access token for authorization
     * @return 201 Created or appropriate error response
     */
    @Operation(summary = "Add teacher comment", description = "Adds a new comment for a teacher. Requires authentication.")
    @ApiResponse(responseCode = "201", description = "Comment added successfully")
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

            commentsRepository.addNewTeacherComment(userId, teacherId, description, rating, levelAccess);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR, "Error adding new teacher comment: " + e.getMessage());
          //  e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes a subject comment by its ID.
     *
     * @param commentId the ID of the comment to delete
     * @return 204 No Content or appropriate error response
     */
    @Operation(summary = "Delete subject comment", description = "Deletes a subject comment by its ID.")
    @ApiResponse(responseCode = "204", description = "Comment deleted successfully")
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

    /**
     * Deletes a teacher comment by its ID.
     *
     * @param commentId the ID of the comment to delete
     * @return 204 No Content or appropriate error response
     */
    @Operation(summary = "Delete teacher comment", description = "Deletes a teacher comment by its ID.")
    @ApiResponse(responseCode = "204", description = "Comment deleted successfully")
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