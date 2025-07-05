package org.main.unimapapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.main.unimapapi.dtos.Comment_dto;
import org.main.unimapapi.services.CommentsService;
import org.main.unimapapi.utils.ServerLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Map;

/**
 * REST controller for comments operations of the UniMap server.
 *
 * <p>This controller provides endpoints for managing comments related to subjects and teachers.
 * It supports operations for retrieving, creating, and deleting comments with proper
 * authorization validation.</p>
 *
 * <p><strong>Base URL:</strong> <code>/api/unimap_pc/comments</code></p>
 *
 * <p><strong>Authentication:</strong> All endpoints require valid Authorization header with access token.</p>
 *
 * <p><strong>Supported Operations:</strong></p>
 * <ul>
 *   <li>Get all comments for a specific subject</li>
 *   <li>Get all comments for a specific teacher</li>
 *   <li>Add new comment for a subject</li>
 *   <li>Add new comment for a teacher</li>
 *   <li>Delete comment for a subject</li>
 *   <li>Delete comment for a teacher</li>
 * </ul>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@Tag(name = "Comments Management", description = "Endpoints for managing comments on subjects and teachers")
@RestController
@RequestMapping("/api/unimap_pc/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentsController extends BaseController {
    private final CommentsService commentsService;

    /**
     * Retrieves all comments for a specific subject.
     *
     * <p>This endpoint returns a list of all comments associated with the specified subject ID.
     * The requester must provide a valid authorization token to access this information.</p>
     *
     * @param subjectId the unique identifier of the subject (must not be null or empty)
     * @param accessToken the authorization token for authentication (must be valid)
     * @return {@link ResponseEntity} containing a list of {@link Comment_dto} objects
     *         representing comments for the specified subject
     *
     * @throws IllegalArgumentException if subjectId is null or empty
     * @throws SecurityException if the access token is invalid
     * @throws RuntimeException if there's an error retrieving comments from the database
     *
     * @apiNote Returns HTTP 200 OK with comment list on success,
     *          HTTP 401 Unauthorized if token is invalid,
     *          HTTP 500 Internal Server Error if processing fails
     */
    @Operation(
            summary = "Get all comments for a subject",
            description = "Retrieves all comments associated with the specified subject ID. " +
                    "Requires valid authorization token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Comment_dto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing access token"),
            @ApiResponse(responseCode = "404", description = "Subject not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/subject/{subject_id}")
    public ResponseEntity<List<Comment_dto>> getAllSubjectsComments(
            @Parameter(description = "Unique identifier of the subject", required = true, example = "MATH101")
            @PathVariable("subject_id") String subjectId,
            @Parameter(description = "Authorization token", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String accessToken) {

        try {
            // Log incoming request
            ServerLogger.logServer(ServerLogger.Level.INFO,
                    "Received request to get comments for subject: " + subjectId);

            // Validate input parameters
            if (subjectId == null || subjectId.trim().isEmpty()) {
                ServerLogger.logServer(ServerLogger.Level.WARNING,
                        "Invalid subject ID provided: " + subjectId);
                return ResponseEntity.badRequest().build();
            }

            // Validate authorization token
            if (!isValidToken(accessToken)) {
                ServerLogger.logServer(ServerLogger.Level.WARNING,
                        "Unauthorized access attempt for subject comments: " + subjectId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Retrieve comments from service
            List<Comment_dto> subjectsCommentsList = commentsService.getSubjectComments(subjectId);

            ServerLogger.logServer(ServerLogger.Level.INFO,
                    "Successfully retrieved " + subjectsCommentsList.size() + " comments for subject: " + subjectId);

            return ResponseEntity.ok(subjectsCommentsList);

        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR,
                    "Error retrieving comments for subject " + subjectId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves all comments for a specific teacher.
     *
     * <p>This endpoint returns a list of all comments associated with the specified teacher ID.
     * The requester must provide a valid authorization token to access this information.</p>
     *
     * @param teacherId the unique identifier of the teacher (must not be null or empty)
     * @param accessToken the authorization token for authentication (must be valid)
     * @return {@link ResponseEntity} containing a list of {@link Comment_dto} objects
     *         representing comments for the specified teacher
     *
     * @throws IllegalArgumentException if teacherId is null or empty
     * @throws SecurityException if the access token is invalid
     * @throws RuntimeException if there's an error retrieving comments from the database
     *
     * @apiNote Returns HTTP 200 OK with comment list on success,
     *          HTTP 401 Unauthorized if token is invalid,
     *          HTTP 500 Internal Server Error if processing fails
     */
    @Operation(
            summary = "Get all comments for a teacher",
            description = "Retrieves all comments associated with the specified teacher ID. " +
                    "Requires valid authorization token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Comment_dto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing access token"),
            @ApiResponse(responseCode = "404", description = "Teacher not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/teacher/{teacher_id}")
    public ResponseEntity<List<Comment_dto>> getAllTeachersComments(
            @Parameter(description = "Unique identifier of the teacher", required = true, example = "TEACHER123")
            @PathVariable("teacher_id") String teacherId,
            @Parameter(description = "Authorization token", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String accessToken) {

        try {
            // Log incoming request
            ServerLogger.logServer(ServerLogger.Level.INFO,
                    "Received request to get comments for teacher: " + teacherId);

            // Validate input parameters
            if (teacherId == null || teacherId.trim().isEmpty()) {
                ServerLogger.logServer(ServerLogger.Level.WARNING,
                        "Invalid teacher ID provided: " + teacherId);
                return ResponseEntity.badRequest().build();
            }

            // Validate authorization token
            if (!isValidToken(accessToken)) {
                ServerLogger.logServer(ServerLogger.Level.WARNING,
                        "Unauthorized access attempt for teacher comments: " + teacherId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Retrieve comments from service
            List<Comment_dto> teachersCommentsList = commentsService.getTeacherComments(teacherId);

            ServerLogger.logServer(ServerLogger.Level.INFO,
                    "Successfully retrieved " + teachersCommentsList.size() + " comments for teacher: " + teacherId);

            return ResponseEntity.ok(teachersCommentsList);

        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR,
                    "Error retrieving comments for teacher " + teacherId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Adds a new comment for a subject.
     *
     * <p>This endpoint creates a new comment associated with a subject. The comment data
     * should be provided in the request body as a JSON object. The requester must provide
     * a valid authorization token.</p>
     *
     * @param payload the comment data as a map containing comment details (must not be null)
     * @param accessToken the authorization token for authentication (must be valid)
     * @return {@link ResponseEntity} with HTTP 201 Created status on success
     *
     * @throws ValidationException if the payload is invalid or missing required fields
     * @throws SecurityException if the access token is invalid
     * @throws RuntimeException if there's an error saving the comment to the database
     *
     * @apiNote Returns HTTP 201 Created on success,
     *          HTTP 400 Bad Request if payload is invalid,
     *          HTTP 401 Unauthorized if token is invalid,
     *          HTTP 500 Internal Server Error if processing fails
     */
    @Operation(
            summary = "Add new comment for a subject",
            description = "Creates a new comment associated with a subject. " +
                    "Requires valid authorization token and comment data in request body.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing access token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/subject")
    public ResponseEntity<Void> addNewSubjectComment(
            @Parameter(description = "Comment data", required = true)
            @Valid @RequestBody Map<String, Object> payload,
            @Parameter(description = "Authorization token", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String accessToken) {

        try {
            // Log incoming request
            ServerLogger.logServer(ServerLogger.Level.INFO,
                    "Received request to add new subject comment");

            // Validate input parameters
            if (payload == null || payload.isEmpty()) {
                ServerLogger.logServer(ServerLogger.Level.WARNING,
                        "Invalid payload provided for subject comment");
                return ResponseEntity.badRequest().build();
            }

            // Validate authorization token
            if (!isValidToken(accessToken)) {
                ServerLogger.logServer(ServerLogger.Level.WARNING,
                        "Unauthorized access attempt to add subject comment");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Add comment through service
            commentsService.addComment(payload, "subject");

            ServerLogger.logServer(ServerLogger.Level.INFO,
                    "Successfully added new subject comment");

            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (ValidationException e) {
            ServerLogger.logServer(ServerLogger.Level.WARNING,
                    "Validation error when adding subject comment: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR,
                    "Error adding subject comment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Adds a new comment for a teacher.
     *
     * <p>This endpoint creates a new comment associated with a teacher. The comment data
     * should be provided in the request body as a JSON object. The requester must provide
     * a valid authorization token.</p>
     *
     * @param payload the comment data as a map containing comment details (must not be null)
     * @param accessToken the authorization token for authentication (must be valid)
     * @return {@link ResponseEntity} with HTTP 201 Created status on success
     *
     * @throws ValidationException if the payload is invalid or missing required fields
     * @throws SecurityException if the access token is invalid
     * @throws RuntimeException if there's an error saving the comment to the database
     *
     * @apiNote Returns HTTP 201 Created on success,
     *          HTTP 400 Bad Request if payload is invalid,
     *          HTTP 401 Unauthorized if token is invalid,
     *          HTTP 500 Internal Server Error if processing fails
     */
    @Operation(
            summary = "Add new comment for a teacher",
            description = "Creates a new comment associated with a teacher. " +
                    "Requires valid authorization token and comment data in request body.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing access token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/teacher")
    public ResponseEntity<Void> addNewTeacherComment(
            @Parameter(description = "Comment data", required = true)
            @Valid @RequestBody Map<String, Object> payload,
            @Parameter(description = "Authorization token", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String accessToken) {

        try {
            // Log incoming request
            ServerLogger.logServer(ServerLogger.Level.INFO,
                    "Received request to add new teacher comment");

            // Validate input parameters
            if (payload == null || payload.isEmpty()) {
                ServerLogger.logServer(ServerLogger.Level.WARNING,
                        "Invalid payload provided for teacher comment");
                return ResponseEntity.badRequest().build();
            }

            // Validate authorization token
            if (!isValidToken(accessToken)) {
                ServerLogger.logServer(ServerLogger.Level.WARNING,
                        "Unauthorized access attempt to add teacher comment");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Add comment through service
            commentsService.addComment(payload, "teacher");

            ServerLogger.logServer(ServerLogger.Level.INFO,
                    "Successfully added new teacher comment");

            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (ValidationException e) {
            ServerLogger.logServer(ServerLogger.Level.WARNING,
                    "Validation error when adding teacher comment: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR,
                    "Error adding teacher comment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes a comment for a subject.
     *
     * <p>This endpoint removes a comment associated with a subject using the comment ID.
     * The requester must provide a valid authorization token and have appropriate permissions
     * to delete the comment.</p>
     *
     * @param commentId the unique identifier of the comment to delete (must be positive)
     * @param accessToken the authorization token for authentication (must be valid)
     * @return {@link ResponseEntity} with HTTP 204 No Content status on success
     *
     * @throws IllegalArgumentException if commentId is not positive
     * @throws SecurityException if the access token is invalid
     * @throws RuntimeException if there's an error deleting the comment from the database
     *
     * @apiNote Returns HTTP 204 No Content on success,
     *          HTTP 400 Bad Request if commentId is invalid,
     *          HTTP 401 Unauthorized if token is invalid,
     *          HTTP 404 Not Found if comment doesn't exist,
     *          HTTP 500 Internal Server Error if processing fails
     */
    @Operation(
            summary = "Delete a subject comment",
            description = "Removes a comment associated with a subject using the comment ID. " +
                    "Requires valid authorization token and appropriate permissions.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid comment ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing access token"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/subject/{comment_id}")
    public ResponseEntity<Void> deleteSubjectComment(
            @Parameter(description = "Unique identifier of the comment", required = true, example = "123")
            @PathVariable("comment_id") int commentId,
            @Parameter(description = "Authorization token", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String accessToken) {

        try {
            // Log incoming request
            ServerLogger.logServer(ServerLogger.Level.INFO,
                    "Received request to delete subject comment with ID: " + commentId);

            // Validate input parameters
            if (commentId <= 0) {
                ServerLogger.logServer(ServerLogger.Level.WARNING,
                        "Invalid comment ID provided: " + commentId);
                return ResponseEntity.badRequest().build();
            }

            // Validate authorization token
            if (!isValidToken(accessToken)) {
                ServerLogger.logServer(ServerLogger.Level.WARNING,
                        "Unauthorized access attempt to delete subject comment: " + commentId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Delete comment through service
            commentsService.deleteSubjectComment(commentId);

            ServerLogger.logServer(ServerLogger.Level.INFO,
                    "Successfully deleted subject comment with ID: " + commentId);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR,
                    "Error deleting subject comment " + commentId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes a comment for a teacher.
     *
     * <p>This endpoint removes a comment associated with a teacher using the comment ID.
     * The requester must provide a valid authorization token and have appropriate permissions
     * to delete the comment.</p>
     *
     * @param commentId the unique identifier of the comment to delete (must be positive)
     * @param accessToken the authorization token for authentication (must be valid)
     * @return {@link ResponseEntity} with HTTP 204 No Content status on success
     *
     * @throws IllegalArgumentException if commentId is not positive
     * @throws SecurityException if the access token is invalid
     * @throws RuntimeException if there's an error deleting the comment from the database
     *
     * @apiNote Returns HTTP 204 No Content on success,
     *          HTTP 400 Bad Request if commentId is invalid,
     *          HTTP 401 Unauthorized if token is invalid,
     *          HTTP 404 Not Found if comment doesn't exist,
     *          HTTP 500 Internal Server Error if processing fails
     */
    @Operation(
            summary = "Delete a teacher comment",
            description = "Removes a comment associated with a teacher using the comment ID. " +
                    "Requires valid authorization token and appropriate permissions.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid comment ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing access token"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/teacher/{comment_id}")
    public ResponseEntity<Void> deleteTeacherComment(
            @Parameter(description = "Unique identifier of the comment", required = true, example = "456")
            @PathVariable("comment_id") int commentId,
            @Parameter(description = "Authorization token", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String accessToken) {

        try {
            // Log incoming request
            ServerLogger.logServer(ServerLogger.Level.INFO,
                    "Received request to delete teacher comment with ID: " + commentId);

            // Validate input parameters
            if (commentId <= 0) {
                ServerLogger.logServer(ServerLogger.Level.WARNING,
                        "Invalid comment ID provided: " + commentId);
                return ResponseEntity.badRequest().build();
            }

            // Validate authorization token
            if (!isValidToken(accessToken)) {
                ServerLogger.logServer(ServerLogger.Level.WARNING,
                        "Unauthorized access attempt to delete teacher comment: " + commentId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Delete comment through service
            commentsService.deleteTeacherComment(commentId);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR,
                    "Error deleting teacher comment " + commentId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}