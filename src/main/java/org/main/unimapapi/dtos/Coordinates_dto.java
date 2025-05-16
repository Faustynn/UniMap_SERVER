package org.main.unimapapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing geographical coordinates.
 *
 * <p>Used to store or transmit location information via latitude and longitude values.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates_dto {

    /**
     * The latitude component of the coordinate (e.g., 48.1486 for Bratislava).
     */
    private double latitude;

    /**
     * The longitude component of the coordinate (e.g., 17.1077 for Bratislava).
     */
    private double longitude;

}
