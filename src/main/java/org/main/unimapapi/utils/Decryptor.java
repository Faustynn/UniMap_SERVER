package org.main.unimapapi.utils;

import org.main.unimapapi.configs.AppConfig;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


public class Decryptor {
    private static final String AES_ALGORITHM = AppConfig.getAesAlgorithm();
    private static final String SECRET_KEY = AppConfig.getSecretKey();
    private static final String IV = AppConfig.getIv();


    public static String decrypt(String encryptedData) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));

        return new String(original);
    }
}
