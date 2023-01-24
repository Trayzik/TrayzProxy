package pl.trayz.proxy.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Author: Trayz
 **/

/**
 * CryptUtil is a class that contains methods for encrypting and decrypting data from minecraft authentication.
 */
public class CryptUtil {
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(1024);
            return gen.generateKeyPair();
        } catch(NoSuchAlgorithmException e) {
            throw new Error("Failed to generate key pair.", e);
        }
    }

    public static SecretKey decryptSharedKey(PrivateKey privateKey, byte[] sharedKey) {
        return new SecretKeySpec(decryptData(privateKey, sharedKey), "AES");
    }

    public static byte[] decryptData(Key key, byte[] data) {
        return runEncryption(2, key, data);
    }

    private static byte[] runEncryption(int mode, Key key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(mode, key);
            return cipher.doFinal(data);
        } catch(GeneralSecurityException e) {
            throw new Error("Failed to run encryption.", e);
        }
    }

    public static Cipher createNetCipherInstance(int opMode, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch (GeneralSecurityException var3) {
            throw new RuntimeException(var3);
        }
    }
}
