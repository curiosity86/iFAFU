package cn.ifafu.ifafu.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {

    public static String md5(String str) {
        try {
            return tc(MessageDigest.getInstance("MD5").digest(str.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException unused) {
            return "";
        }
    }

    private static String tc(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(255 & b);
            if (hexString.length() == 1) {
                sb.append('0');
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

}
