package org.main.unimapapi.entities;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Subject {
    private String code;
    private String name;
    private String type;
    private int credits;
    private String studyType;
    private String semester;
    private List<String> languages;
    private String completionType;
    private long studentCount;
    private List<EvaluationEntity> evaluation;
    private String assesmentMethods;
    private String learningOutcomes;
    private String courseContents;
    private String plannedActivities;
    private String evaluationMethods;
    private List<SubjectTeacherEntity> teacherList;
}
