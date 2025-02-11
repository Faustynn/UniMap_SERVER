package org.main.unimapapi.utils;

import org.main.unimapapi.configs.AppConfig;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Encryptor {
    private static final String AES_ALGORITHM = AppConfig.getAesAlgorithm();
    private static final String SECRET_KEY = AppConfig.getSecretKey();
    private static final String IV = AppConfig.getIv();

    public static String encrypt(String data) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

}
