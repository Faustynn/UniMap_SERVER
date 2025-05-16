package org.main.unimapapi.repository_queries;

import org.main.unimapapi.dtos.Subject_dto;
import org.main.unimapapi.dtos.TeacherSubjectRoles;
import org.main.unimapapi.dtos.Teacher_dto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Repository class for fetching subject and teacher data for UI display.
 *
 * <p>Used by:</p>
 * <ul>
 *     <li>{@code SubjectController} → {@code /resources/subjects}</li>
 *     <li>{@code TeacherController} → {@code /resources/teachers}</li>
 * </ul>
 */
@Repository
@RequiredArgsConstructor
public class DataFatcherRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * RowMapper for mapping subject records with evaluation statistics (A–Fx).
     */
    private final RowMapper<Subject_dto> subjectsRowMapper = (rs, rowNum) -> {
        Subject_dto subject = new Subject_dto();
        subject.setCode(rs.getString("code"));
        subject.setName(rs.getString("name"));
        subject.setType(rs.getString("type"));
        subject.setCredits(rs.getInt("credits"));
        subject.setStudyType(rs.getString("study_type"));
        subject.setSemester(rs.getString("semester"));
        subject.setLanguages(Arrays.asList(rs.getString("languages").split(",")));
        subject.setCompletionType(rs.getString("completion_type"));
        subject.setStudentCount(rs.getInt("student_count"));
        subject.setAssesmentMethods(rs.getString("assesment_methods"));
        subject.setLearningOutcomes(rs.getString("learning_outcomes"));
        subject.setCourseContents(rs.getString("course_contents"));
        subject.setPlannedActivities(rs.getString("planned_activities"));
        subject.setEvaluationMethods(rs.getString("evaluation_methods"));

        subject.setAscore(rs.getString("a_score"));
        subject.setBscore(rs.getString("b_score"));
        subject.setCscore(rs.getString("c_score"));
        subject.setDscore(rs.getString("d_score"));
        subject.setEscore(rs.getString("e_score"));
        subject.setFXscore(rs.getString("fx_score"));
        return subject;
    };

    /**
     * Fetches all subjects, joined with grade distributions from {@code subject_evaluation}.
     *
     * @return a list of {@link Subject_dto} including grade stats
     */
    public List<Subject_dto> fetchAllSubjects() {
        String sql = """
                SELECT\s
                    sub.*,
                    MAX(CASE WHEN sub_eval.grade = 'A' THEN sub_eval.percent END) AS a_score,
                    MAX(CASE WHEN sub_eval.grade = 'B' THEN sub_eval.percent END) AS b_score,
                    MAX(CASE WHEN sub_eval.grade = 'C' THEN sub_eval.percent END) AS c_score,
                    MAX(CASE WHEN sub_eval.grade = 'D' THEN sub_eval.percent END) AS d_score,
                    MAX(CASE WHEN sub_eval.grade = 'E' THEN sub_eval.percent END) AS e_score,
                    MAX(CASE WHEN sub_eval.grade = 'Fx' THEN sub_eval.percent END) AS fx_score
                FROM\s
                    subjects sub
                LEFT JOIN\s
                    subject_evaluation sub_eval ON sub.code = sub_eval.subject_code
                GROUP BY\s
                    sub.code""";
        return jdbcTemplate.query(sql, subjectsRowMapper);
    }





    /**
     * RowMapper for mapping teachers and their subject roles.
     * <p>Assumes one row per teacher-subject pair. If no roles are found, assigns empty list.</p>
     */
    private final RowMapper<Teacher_dto> teachersRowMapper = (rs, rowNum) -> {
        Teacher_dto teacher = new Teacher_dto();
        teacher.setId(rs.getString("id"));
        teacher.setName(rs.getString("name"));
        teacher.setEmail(rs.getString("email"));
        teacher.setPhone(rs.getString("phone"));
        teacher.setOffice(rs.getString("office"));

        String subjectCode = rs.getString("subject_code");
        String roles = rs.getString("roles");

        if (subjectCode != null && roles != null) {
            TeacherSubjectRoles tsr = new TeacherSubjectRoles();
            tsr.setSubjectName(subjectCode);
            tsr.setRoles(Arrays.asList(roles.split(",")));
            teacher.setSubjects(Collections.singletonList(tsr));
        } else {
            teacher.setSubjects(Collections.emptyList());
        }

        return teacher;
    };

    /**
     * Fetches all teachers along with their subject roles.
     *
     * @return a list of {@link Teacher_dto} with assigned subjects and roles
     */
    public List<Teacher_dto> fetchAllTeachers() {
        String sql = """
                SELECT tea.id, tea.name, tea.email, tea.phone, tea.office,
                       tea_sub.subject_code, tea_sub.roles
                FROM teachers tea
                LEFT JOIN teacher_subject_roles tea_sub ON tea.id = tea_sub.teacher_id
                ORDER BY tea.id;""";
        return jdbcTemplate.query(sql, teachersRowMapper);
    }
}