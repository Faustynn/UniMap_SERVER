package org.main.unimapapi.entities;

import lombok.*;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    private Long id;
    private String deviceId;
    private String pushToken;
    private String platform;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime lastActiveAt;
}