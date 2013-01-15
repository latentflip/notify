package com.latentflip.notify;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
import java.security.SecureRandom;
import java.math.BigInteger;

public enum AESUtil {
    ;

    private static final SecureRandom random = new SecureRandom();

    private static final String ENCRYPTION_KEY = "RwcmlVpg";
    private static final String ENCRYPTION_IV = "4e5Wa71fYoT7MFEX";

    public static HashMap<String, String> encryptWithIV(String src) {
        HashMap<String, String> encryptionMap = new HashMap<String, String>();

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encryptionMap.put("iv", ENCRYPTION_IV);
            cipher.init(Cipher.ENCRYPT_MODE, makeKey(), makeIv(encryptionMap.get("iv")));
            encryptionMap.put("encrypted", Base64.encodeToString(cipher.doFinal(src.getBytes()), Base64.NO_WRAP));

            return encryptionMap;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String randomIVString() {
        return new BigInteger(130, random).toString(16);
    }

    public static String encrypt(String src) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, makeKey(), makeIv());
            return Base64.encodeToString(cipher.doFinal(src.getBytes()), Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String src) {
        String decrypted = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, makeKey(), makeIv());
            decrypted = new String(cipher.doFinal(Base64.decode(src, Base64.NO_WRAP)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return decrypted;
    }

    static AlgorithmParameterSpec makeIv() {
        return makeIv(ENCRYPTION_IV);
    }

    static AlgorithmParameterSpec makeIv(String ivString) {
        try {
            return new IvParameterSpec(ivString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Key makeKey() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] key = md.digest(ENCRYPTION_KEY.getBytes("UTF-8"));
            return new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}