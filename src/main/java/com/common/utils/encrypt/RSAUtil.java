package com.common.utils.encrypt;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtil {

    /**
     * 指定加密算法为RSA
     */
    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";
    /**
     * 指定公钥存放文件
     */
    public static String PUBLIC_KEY_FILE = "PublicKey";
    /**
     * 指定私钥存放文件
     */
    public static String PRIVATE_KEY_FILE = "PrivateKey";

    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static void main(String[] args) throws Exception {

        String source = "你好nihao";// 要加密的字符串  
        System.out.println("准备用公钥加密的字符串为：" + source);

        String cryptograph = encrypt("", source);// 生成的密文
        System.out.print("用公钥加密后的结果为:" + cryptograph);
        System.out.println();

        String target = decrypt("", cryptograph);// 解密密文
        System.out.println("用私钥解密后的字符串为：" + target);
        System.out.println();
    }

    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return new String(Base64.getEncoder().encode(signature.sign()));
    }

    /**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.getDecoder().decode(sign));
    }

    /**
     * 加密方法
     *
     * @param source 源数据
     * @return
     * @throws Exception
     */
    public static String encrypt(String path, String source) throws Exception {

        Key publicKey = getKey(path);

        /** 得到Cipher对象来实现对源数据的RSA加密 */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        //byte[] b = source.getBytes();
        /** 执行加密操作 */
        //byte[] b1 = cipher.doFinal(b);
        byte[] dataByteArray = source.getBytes();
        int inputLen = dataByteArray.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(dataByteArray, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataByteArray, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        return new String(Base64.getEncoder().encode(out.toByteArray()));
    }

    /**
     * 解密算法
     *
     * @param cryptograph 密文
     * @return
     * @throws Exception
     */
    public static String decrypt(String path, String cryptograph) throws Exception {

        Key privateKey = getKey(path);

        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] b1 = decoder.decode(cryptograph);
        /** 执行解密操作 */
//        byte[] b = cipher.doFinal(b1);
        int inputLen = b1.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(b1, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(b1, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        return new String(decryptedData);
    }

    private static Key getKey(String fileName) throws Exception {
        Key key;
        ObjectInputStream ois = null;
        try {
            /** 将文件中的私钥对象读出 */
            ois = new ObjectInputStream(new FileInputStream(fileName));
            key = (Key) ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
        }
        return key;
    }

    public static String getPublicKeyBase64(String path) {
        FileInputStream is = null;
        try {
            /** 将文件中的私钥对象读出 */
            is = new FileInputStream(path);
            byte[] temp = new byte[10 * 1024];
            int len;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            while ((len = is.read(temp)) >= 0) {
                os.write(temp, 0, len);
            }
            return new String(Base64.getEncoder().encode(os.toByteArray()));
        } catch (Exception e) {
            return null;
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }
}  