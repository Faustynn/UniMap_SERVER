package org.main.unimapapi.services;

import lombok.RequiredArgsConstructor;
import org.main.unimapapi.repository_queries.CommentsRepository;
import org.main.unimapapi.dtos.Comment_dto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentsService {
    private final CommentsRepository commentsRepository;

    public List<Comment_dto> getSubjectComments(String subjectId) {
        return commentsRepository.getAllSubjectsComments(subjectId);
    }

    public List<Comment_dto> getTeacherComments(String teacherId) {
        return commentsRepository.getAllTeachersComments(teacherId);
    }

    public void addComment(Map<String, Object> payload, String type) {
        int userId = Integer.parseInt(String.valueOf(payload.get("user_id")));
        String subjectCode = String.valueOf(payload.get("code"));
        String description = String.valueOf(payload.get("text"));
        int rating = Integer.parseInt(String.valueOf(payload.get("rating")));
        int levelAccess = Integer.parseInt(String.valueOf(payload.get("levelAccess")));

        if(type.equals("subject")) {
            commentsRepository.addNewSubjectComment(userId, subjectCode, description, rating, levelAccess);
            return;
        }else if(type.equals("teacher")) {
            commentsRepository.addNewTeacherComment(userId, subjectCode, description, rating, levelAccess);
        }
    }

    public void deleteSubjectComment(int commentId) {
        commentsRepository.deleteSubjectComment(commentId);
    }

    public void deleteTeacherComment(int commentId) {
        commentsRepository.deleteTeacherComment(commentId);
    }
}