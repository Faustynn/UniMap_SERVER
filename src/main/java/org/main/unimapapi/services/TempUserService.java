package org.main.unimapapi.services;

import org.main.unimapapi.models.TempUser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TempUserService {
    private final Map<Long, TempUser> tempUsers = new ConcurrentHashMap<>();
    private final UserService userService;

    public void addTempUser(Long idUser, int deleteAfterMinutes) {
        tempUsers.put(idUser, new TempUser(idUser, LocalDateTime.now(), deleteAfterMinutes));
    }

    public boolean exists(Long idUser) {
        return tempUsers.containsKey(idUser);
    }

    @Scheduled(fixedRate = 5_000)
    public void cleanUpExpiredUsers() {
        LocalDateTime now = LocalDateTime.now();
        tempUsers.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().getCreatedAt()
                    .plusMinutes(entry.getValue().getDeleteAfterMinutes())
                    .isBefore(now);

            if (expired) {
                Long id = entry.getKey();
                userService.delete(id);
                System.out.println("[DEBUG] Temporary user and User with ID deleted: " + id);
            }

            return expired;
        });
    }
}
