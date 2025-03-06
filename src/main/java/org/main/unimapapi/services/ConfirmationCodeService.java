package org.main.unimapapi.services;

import org.main.unimapapi.entities.ConfirmationCode;
import org.main.unimapapi.repository_queries.ConfirmationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class ConfirmationCodeService {
    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    public void save(ConfirmationCode confirmationCode) {
        confirmationCodeRepository.save(confirmationCode);
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
        try {
            System.out.println("VALIDATE CONFIRMATION CODE "+userId+" "+code);
            if (confirmationCodeRepository.find(userId,code)) {
                confirmationCodeRepository.deleteByUserId(userId,code);
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
}