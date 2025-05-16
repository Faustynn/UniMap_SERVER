package org.main.unimapapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing detailed information about a subject/course.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subject_dto {

    /**
     * The unique code of the subject (e.g., {@code "MTAA"}).
     */
    private String code;

    /**
     * The full name of the subject (e.g., {@code "Mobile Applications"}).
     */
    private String name;

    /**
     * The type of subject (e.g., {@code "compulsory"}, {@code "elective"}).
     */
    private String type;

    /**
     * Number of ECTS credits awarded for the subject.
     */
    private int credits;

    /**
     * Study program type (e.g., {@code "Bachelor"}, {@code "Master"}).
     */
    private String studyType;

    /**
     * Semester when the subject is taught (e.g., {@code "Summer 2025"}).
     */
    private String semester;

    /**
     * List of supported teaching languages (e.g., {@code ["SK", "EN"]}).
     */
    private List<String> languages;

    /**
     * Completion type (e.g., {@code "Exam"}, {@code "Assessment"}).
     */
    private String completionType;

    /**
     * Number of students enrolled in the subject.
     */
    private long studentCount;

    /**
     * List of evaluations (e.g., grade + percentage pairs).
     */
    private List<Evaluation> evaluation;

    /**
     * Description of how the subject will be assessed.
     */
    private String assesmentMethods;

    /**
     * Description of learning outcomes for students.
     */
    private String learningOutcomes;

    /**
     * Outline of the course contents.
     */
    private String courseContents;

    /**
     * Planned educational activities (lectures, labs, etc.).
     */
    private String plannedActivities;

    /**
     * Methods by which the student's performance will be evaluated.
     */
    private String evaluationMethods;

    /**
     * Percentage of students who achieved grade A.
     */
    private String ascore;

    /**
     * Percentage of students who achieved grade B.
     */
    private String bscore;

    /**
     * Percentage of students who achieved grade C.
     */
    private String cscore;

    /**
     * Percentage of students who achieved grade D.
     */
    private String dscore;

    /**
     * Percentage of students who achieved grade E.
     */
    private String escore;

    /**
     * Percentage of students who failed (grade FX).
     */
    private String FXscore;
}
