package com.common.utils.encrypt;

import android.content.Context;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.security.Key;
import java.util.UUID;

public class AppSafeCore {

    private Context context;

    private File root;

    private AppSafeCore() {
    }

    public static AppSafeCore getInstance() {
        return Inner.sInstance;
    }

    private static class Inner {
        private static AppSafeCore sInstance = new AppSafeCore();
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        root = new File(this.context.getFilesDir().getAbsolutePath() + File.separator + "cer");
        if (!isInit()) {
            try {
                KeyPairGenUtil.generateKeyPair(root.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean resetCer() {
        try {
            KeyPairGenUtil.generateKeyPair(root.getAbsolutePath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void initDeviceIdentify(String aaid, String androidId) {
        String id = aaid + "===" + androidId + "===" + UUID.randomUUID().toString();
    }

    public boolean isInit() {
        if (!root.exists() || !root.isDirectory()) {
            root.mkdirs();
        }
        File publicKey = new File(root.getAbsolutePath() + File.separator + RSAUtil.PUBLIC_KEY_FILE);
        File privateKey = new File(root.getAbsolutePath() + File.separator + RSAUtil.PRIVATE_KEY_FILE);
        if (!publicKey.exists() || !privateKey.exists()) {
            return false;
        }
        return true;
    }

    public String encryptData(String data) {
        try {
            return RSAUtil.encrypt(root.getAbsolutePath() + File.separator + RSAUtil.PUBLIC_KEY_FILE, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decryptData(String data) {
        try {
            return RSAUtil.decrypt(root.getAbsolutePath() + File.separator + RSAUtil.PRIVATE_KEY_FILE, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPublicKey() {
        try {
            return RSAUtil.getPublicKeyBase64(root.getAbsolutePath() + File.separator + RSAUtil.PUBLIC_KEY_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getPublicKeyBytes() {
        try {
            String publicKeyBs64 = RSAUtil.getPublicKeyBase64(root.getAbsolutePath() + File.separator + RSAUtil.PUBLIC_KEY_FILE);
            if (publicKeyBs64 != null && publicKeyBs64.length() > 0) {
                byte[] pubBytes = Base64.getDecoder().decode(publicKeyBs64);
                ByteArrayInputStream is = new ByteArrayInputStream(pubBytes);
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(is);
                    Key key = (Key) ois.readObject();
                    return key.getEncoded();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ois != null) {
                        ois.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPublicKeyBytes64() {
        byte[] pk = getPublicKeyBytes();
        if (pk != null) {
            return Base64.getEncoder().encodeToString(pk);
        }
        return null;
    }
}
