package cn.ifafu.ifafu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    /**
     * 查找字符串中的数字
     */
    public static List<Integer> getNumbers(String str) {
        List<Integer> ans = new ArrayList<>();
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(str);
        while (m.find()) {
            ans.add(Integer.parseInt(m.group()));
        }
        return ans;
    }

}
