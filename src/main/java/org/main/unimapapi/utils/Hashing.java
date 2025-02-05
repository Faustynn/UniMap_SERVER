package org.main.unimapapi.utils;

import org.mindrot.jbcrypt.BCrypt;

public class Hashing {

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return org.springframework.security.crypto.bcrypt.BCrypt.checkpw(plainPassword, hashedPassword);
    }



}
