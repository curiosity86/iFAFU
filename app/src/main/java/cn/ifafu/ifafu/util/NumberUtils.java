package cn.ifafu.ifafu.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {
    private static final String[] DIGITS = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    private static final Pattern ENGLISH_DECIMAL_PATTERN = Pattern.compile("([0-9]*)\\.([0-9]+)");
    private static final Pattern ENGLISH_FRACTION_PATTERN = Pattern.compile("([0-9]*)/([0-9]+)");

    private static final String[] BEFORE_WAN_DIGITS = {"十", "百", "千"};

    private static final String[] AFTER_WAN_DIGITS = {"", "萬", "億", "兆", "京"};

    private static final String MINUS = "负";
    private static final String DECIMAL = "点";
    private static final String FRACTION = "分之";

    public static String numberToChinese(int number) {
        return numberToChinese(String.valueOf(number));
    }

    /**
     * 将英文表示的数字转化为中文表示的数字，支持负数、小数、不支持分数
     *
     * @param text 英文表示的数字，比如：2009000，5.3，-5.3
     * @return
     */
    public static String numberToChinese(String text) {
        boolean negative = false;
        if (text.length() == 1 && text.charAt(0) == '0') {
            return DIGITS[0];
        }
        if (text.charAt(0) == '-') {
            negative = true;
            text = text.substring(1);
        }
        Matcher m = ENGLISH_DECIMAL_PATTERN.matcher(text);
        String result;
        if (m.find()) {
            result = englishNumberToChineseFull(m.group(1)) + DECIMAL + englishNumberToChineseBrief(m.group(2));
        } else {
            m = ENGLISH_FRACTION_PATTERN.matcher(text);
            if (m.find()) {
                result = englishNumberToChineseFull(m.group(2)) + FRACTION + englishNumberToChineseFull(m.group(1));
            } else {
                result = englishNumberToChineseFull(text);
            }
        }

        if (negative) {
            result = MINUS + result;
        }
        return result;
    }

    /**
     * 直接映射英文数字为中文数字，对应输入的小数部分如此处理
     *
     * @param text
     * @return
     */
    private static String englishNumberToChineseBrief(String text) {
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            result += DIGITS[text.charAt(i) - '0'];
        }
        return result;
    }

    /**
     * 非输入的小数部分需要做更复杂的转换
     *
     * @param text
     * @return
     */
    private static String englishNumberToChineseFull(String text) {
        int power = 0;
        boolean canAddZero = false;
        boolean inZero = false;
        Map<Integer, Integer> powers = new HashMap<>();
        long number = Long.parseLong(text);
        while (Math.pow(10, power) <= number) {
            int value = (int) ((number % (Math.pow(10, power + 1))) / (Math.pow(10, power)));
            powers.put(power, value);
            number -= number % (Math.pow(10, power + 1));
            power++;
        }
        String result = "";
        for (int i = 0; i < power; i++) {
            if (i % 4 == 0) {
                if (powers.get(i) != 0) {
                    inZero = false;
                    canAddZero = true;
                    result = DIGITS[powers.get(i)] + AFTER_WAN_DIGITS[i / 4] + result;
                } else {
                    if (((i + 3 < power) && powers.get(i + 3) != 0) ||
                            ((i + 2 < power) && powers.get(i + 2) != 0) ||
                            ((i + 1 < power) && powers.get(i + 1) != 0)) {
                        result = AFTER_WAN_DIGITS[i / 4] + result;
                        canAddZero = false;
                    }
                }
            } else {
                if (powers.get(i) != 0) {
                    inZero = false;
                    canAddZero = true;
                    if (power == 2 && i == 1 && powers.get(i) == 1) {
                        result = BEFORE_WAN_DIGITS[(i % 4) - 1] + result;
                    } else {
                        result = DIGITS[powers.get(i)] + BEFORE_WAN_DIGITS[(i % 4) - 1] + result;
                    }
                } else {
                    if (canAddZero && !inZero) {
                        inZero = true;
                        result = DIGITS[powers.get(i)] + result;
                    }
                }
            }
        }
        return result;
    }

}