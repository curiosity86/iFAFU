package cn.ifafu.ifafu.util;

public class NumUtils {
    
    public static Float toFloat(String text) {
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Float.parseFloat(text);
        } catch (Exception e) {
            return null;
        }
    }

    public static Double toDouble(String text) {
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(text);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer toInt(String text) {
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long toLong(String text) {
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(text);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Short toShort(String text) {
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Short.parseShort(text);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}