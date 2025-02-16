package org.main.unimapapi.services;

import org.main.unimapapi.entities.ConfirmationCode;
import org.main.unimapapi.repository_queries.ConfirmationCodeRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class ConfirmationCodeService {
    @Autowired
    private ConfirmationCodeRepositoryImpl confirmationCodeRepository;

    public Optional<ConfirmationCode> findByUserId(Long userId) {
        return confirmationCodeRepository.findByUserId(userId);
    }

    public void save(ConfirmationCode confirmationCode) {
        confirmationCodeRepository.save(confirmationCode);
    }

    public void deleteByUserId(Long userId) {
        confirmationCodeRepository.deleteByUserId(userId);
    }

    public static String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public boolean validateConfirmationCode(Long userId, String code) {
        Optional<ConfirmationCode> confirmationCode = findByUserId(userId);
        return confirmationCode.isPresent() && confirmationCode.get().getCode().equals(code);
    }
}