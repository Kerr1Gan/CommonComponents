package com.common.utils.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by xwf on 2016/10/30.
 */

public class ThreeDes {

    /**
     * @param args在java中调用sun公司提供的3DES加密解密算法时，需要使
     * 用到$JAVA_HOME/jre/lib/目录下如下的4个jar包：
     * jce.jar
     * security/US_export_policy.jar
     * security/local_policy.jar
     * ext/sunjce_provider.jar
     */

    private static final String ALGORITHM = "DESede"; //定义加密算法,可用 DES,DESede,Blowfish
    private static final String DEFAULT_KEY = "galaxylab";

    //keyByte为加密密钥，长度为24字节
    //src为被加密的数据缓冲区（源）
    public static byte[] encrypt(byte[] keyByte, byte[] src) {
        try {
            //生成密钥
            SecretKey deskey = new SecretKeySpec(keyByte, ALGORITHM);
            //加密
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);//在单一方面的加密或解密
        } catch (NoSuchAlgorithmException e1) {
            // TODO: handle exception
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    //keyByte为加密密钥，长度为24字节
    //src为加密后的缓冲区
    public static byte[] decrypt(byte[] keyByte, byte[] src) {
        try {
            //生成密钥
            SecretKey destKey = new SecretKeySpec(keyByte, ALGORITHM);
            //解密
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.DECRYPT_MODE, destKey);
            return c1.doFinal(src);
        } catch (NoSuchAlgorithmException e1) {
            // TODO: handle exception
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] src) {
        return decrypt(hex(DEFAULT_KEY), src);
    }

    private static String md5Hex(String string) {
        if (string == null || string.length() == 0) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static byte[] hex(String secreteKey) {
        String f = md5Hex(secreteKey);
        byte[] bytes = f.getBytes();
        byte[] enk = new byte[24];
        for (int i = 0; i < 24; i++) {
            enk[i] = bytes[i];
        }
        return enk;
    }
}