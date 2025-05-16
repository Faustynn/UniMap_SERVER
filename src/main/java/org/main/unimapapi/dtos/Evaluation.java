package org.main.unimapapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing an evaluation result, such as a grade and its associated percentage.
 *
 * <p>This can be used for displaying or storing assessment results, scores, or grading scales.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {

    /**
     * The textual representation of the grade (e.g., {@code "A"}, {@code "B"}, {@code "Fx"}).
     */
    private String grade;

    /**
     * The percentage value associated with the grade (e.g., {@code 85} for 85%).
     */
    private Integer percentage;
}