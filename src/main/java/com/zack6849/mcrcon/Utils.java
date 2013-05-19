package com.zack6849.mcrcon;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Date;

public class Utils {

    public static String encrypt(String s) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest alg = MessageDigest.getInstance("SHA-512");
            alg.reset();
            alg.update(s.getBytes(Charset.forName("UTF-8")));

            byte[] digest = alg.digest();
            for (byte b : digest) {
                sb.append(Integer.toHexString(0xFF & b));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getTime() {
        return String.format("[%tm/%<td/%<ty - %<tH:%<tM:%<tS] ", new Date());
    }
}
