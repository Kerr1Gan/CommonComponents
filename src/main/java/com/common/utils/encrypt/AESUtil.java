package com.common.utils.encrypt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

    public static byte[] encrypt(String key, byte[] src) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey privateKey = new SecretKeySpec(key.getBytes(), "AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(src);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(String key, byte[] src) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey privateKey = new SecretKeySpec(key.getBytes(), "AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(src);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
