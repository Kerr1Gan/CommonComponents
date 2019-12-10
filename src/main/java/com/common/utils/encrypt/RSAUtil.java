package com.common.utils.encrypt;

import javax.crypto.Cipher;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.Key;

public class RSAUtil {

    /**
     * 指定加密算法为RSA
     */
    private static final String ALGORITHM = "RSA";
    /**
     * 指定公钥存放文件
     */
    public static String PUBLIC_KEY_FILE = "PublicKey";
    /**
     * 指定私钥存放文件
     */
    public static String PRIVATE_KEY_FILE = "PrivateKey";

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
        byte[] b = source.getBytes();
        /** 执行加密操作 */
        byte[] b1 = cipher.doFinal(b);
        return new String(Base64.getEncoder().encode(b1));
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
        byte[] b = cipher.doFinal(b1);
        return new String(b);
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