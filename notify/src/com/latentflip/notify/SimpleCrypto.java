package com.latentflip.notify;

import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;


/**
 * Usage:
 * <pre>
 * String crypto = SimpleCrypto.encrypt(masterpassword, cleartext)
 * ...
 * String cleartext = SimpleCrypto.decrypt(masterpassword, crypto)
 * </pre>
 * @author ferenc.hechler
 */
public class SimpleCrypto {


  public static List<String> encrypt(String password, String cleartext) throws Exception {
    SecretKey secret = getKey(password);
    /* Encrypt the message. */
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secret);
    AlgorithmParameters params = cipher.getParameters();
    byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
    byte[] ciphertext = cipher.doFinal(cleartext.getBytes("UTF-8"));

    String encryptedString = Base64.encodeToString(ciphertext, Base64.NO_WRAP);
    String ivString = Base64.encodeToString(iv, Base64.NO_WRAP);

    return Arrays.asList(ivString, encryptedString);
  }

  private static SecretKey getKey(String password) throws Exception {
    byte[] salt = "mysalt".getBytes();
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
    SecretKey tmp = factory.generateSecret(spec);
    SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

    return secret;
  }
  
  public static String decrypt(String password, String ivString, String encryptedString) throws Exception {
    SecretKey secret = getKey(password);
    byte[] iv = Base64.decode(ivString, Base64.DEFAULT);
    byte[] encrypted = Base64.decode(encryptedString, Base64.DEFAULT);

    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
    String plaintext = new String(cipher.doFinal(encrypted), "UTF-8");
 
    return plaintext;
  }
}
