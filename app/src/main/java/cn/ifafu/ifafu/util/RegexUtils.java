package cn.ifafu.ifafu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static String REGEX_INT = "[0-9]+";

    /**
     * 查找字符串中的数字
     */
    public static List<Integer> getNumbers(String text) {
        List<Integer> ans = new ArrayList<>();
        Pattern p = Pattern.compile(REGEX_INT);
        Matcher m = p.matcher(text);
        while (m.find()) {
            ans.add(Integer.parseInt(m.group()));
        }
        return ans;
    }

    private static List<String> getStrings(String text, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        List<String> result = new ArrayList<>();
        while (m.find()) {
            result.add(m.group());
        }
        return result;
    }

}
