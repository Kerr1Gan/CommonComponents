package com.common.utils.encrypt;

import android.util.Log;

import com.common.componentes.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.*;

public class KeyPairGenUtil {

    private static final String TAG = "KeyPairGenUtil";

    /**
     * 指定加密算法为RSA
     */
    private static final String ALGORITHM = "RSA";
    /**
     * 密钥长度，用来初始化
     */
    private static final int KEYSIZE = 1024;
    /**
     * 指定公钥存放文件
     */
    public static String PUBLIC_KEY_FILE = "PublicKey";
    /**
     * 指定私钥存放文件
     */
    public static String PRIVATE_KEY_FILE = "PrivateKey";

    public static void main(String[] args) throws Exception {
        generateKeyPair();
        generateKeyPair("");
    }

    /**
     * 生成密钥对
     *
     * @throws Exception
     */
    private static void generateKeyPair() throws Exception {

        //     /** RSA算法要求有一个可信任的随机数源 */  
        //     SecureRandom secureRandom = new SecureRandom();  
        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);

        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        //     keyPairGenerator.initialize(KEYSIZE, secureRandom);  
        keyPairGenerator.initialize(KEYSIZE);

        /** 生成密匙对 */
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        /** 得到公钥 */
        Key publicKey = keyPair.getPublic();

        /** 得到私钥 */
        Key privateKey = keyPair.getPrivate();

        ObjectOutputStream oos1 = null;
        ObjectOutputStream oos2 = null;
        try {
            /** 用对象流将生成的密钥写入文件 */
            oos1 = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE));
            oos2 = new ObjectOutputStream(new FileOutputStream(PRIVATE_KEY_FILE));
            oos1.writeObject(publicKey);
            oos2.writeObject(privateKey);
        } catch (Exception e) {
            throw e;
        } finally {
            /** 清空缓存，关闭文件输出流 */
            try {
                oos1.close();
            } catch (Exception e) {
            }
            try {
                oos2.close();
            } catch (Exception e) {
            }
        }
    }

    public static void generateKeyPair(String path) throws Exception {

        /** RSA算法要求有一个可信任的随机数源 */
        SecureRandom secureRandom = new SecureRandom();

        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);

        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        keyPairGenerator.initialize(KEYSIZE, secureRandom);
        //keyPairGenerator.initialize(KEYSIZE);  

        /** 生成密匙对 */
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        /** 得到公钥 */
        Key publicKey = keyPair.getPublic();

        /** 得到私钥 */
        Key privateKey = keyPair.getPrivate();

        byte[] publicKeyBytes = publicKey.getEncoded();
        byte[] privateKeyBytes = privateKey.getEncoded();

        String publicKeyBase64 = new String(Base64.getEncoder().encode(publicKeyBytes));
        String privateKeyBase64 = new String(Base64.getEncoder().encode(privateKeyBytes));

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "publicKeyBase64.length():" + publicKeyBase64.length());
            Log.i(TAG, "publicKeyBase64:" + publicKeyBase64);

            Log.i(TAG, "privateKeyBase64.length():" + privateKeyBase64.length());
            Log.i(TAG, "privateKeyBase64:" + privateKeyBase64);
        }
        ObjectOutputStream oos1 = null;
        ObjectOutputStream oos2 = null;
        try {
            /** 用对象流将生成的密钥写入文件 */
            oos1 = new ObjectOutputStream(new FileOutputStream(path + File.separator + PUBLIC_KEY_FILE));
            oos2 = new ObjectOutputStream(new FileOutputStream(path + File.separator + PRIVATE_KEY_FILE));
            oos1.writeObject(publicKey);
            oos2.writeObject(privateKey);
        } catch (Exception e) {
            throw e;
        } finally {
            /** 清空缓存，关闭文件输出流 */
            try {
                oos1.close();
            } catch (Exception e) {
            }
            try {
                oos2.close();
            } catch (Exception e) {
            }
        }
    }
}  