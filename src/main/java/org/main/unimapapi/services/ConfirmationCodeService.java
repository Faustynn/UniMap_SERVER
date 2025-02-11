package org.main.unimapapi.services;

import org.main.unimapapi.entities.ConfirmationCode;
import org.main.unimapapi.repositories.ConfirmationCodeRepository;
import org.main.unimapapi.utils.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class ConfirmationCodeService {
    private final ConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    public ConfirmationCodeService(ConfirmationCodeRepository confirmationCodeRepository) {
        this.confirmationCodeRepository = confirmationCodeRepository;
    }

    public void generateConfirmationCode(String email,Long userId) {
        String code = generateRandomCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        ConfirmationCode confirmationCode = new ConfirmationCode();
        confirmationCode.setUserId(userId);
        confirmationCode.setCode(code);
        confirmationCode.setExpirationTime(expirationTime);

        confirmationCodeRepository.save(confirmationCode);
        EmailSender.sendEmail(email,code);

     //   System.out.println("Confirmation code: " + code);
    }

    @Transient
    public boolean validateConfirmationCode(Long userId, String code) {
        Optional<ConfirmationCode> confirmationCode = confirmationCodeRepository.findByUserId(userId);
        if (confirmationCode.isPresent() && confirmationCode.get().getCode().equals(code) && confirmationCode.get().getExpirationTime().isAfter(LocalDateTime.now())) {
            confirmationCodeRepository.delete(confirmationCode.get());
        }
        return confirmationCode.isPresent() && confirmationCode.get().getCode().equals(code) && confirmationCode.get().getExpirationTime().isAfter(LocalDateTime.now());
    }

    public static String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
