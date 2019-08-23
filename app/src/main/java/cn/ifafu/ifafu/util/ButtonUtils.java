package cn.ifafu.ifafu.util;

/**
 * Created by gaop on 16-11-4.
 */
public class ButtonUtils {
    private static final long DEFAULT_DIFF = 1000;
    private static long lastClickTime = 0;
    private static int lastButtonId = -1;

    /**
     * 判断两次点击的间隔，如果小于1200，则认为是多次无效点击
     *
     * @return 点击有效性
     */
    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(-1, DEFAULT_DIFF);
    }

    public static boolean isFastDoubleClick(long diff) {
        return isFastDoubleClick(-1, diff);
    }

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     *
     * @return 点击有效性
     */
    public static boolean isFastDoubleClick(int buttonId) {
        return isFastDoubleClick(buttonId, DEFAULT_DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param diff 间隔时间
     * @return 点击有效性
     */
    public static boolean isFastDoubleClick(int buttonId, long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            lastClickTime = 0;
            return true;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }

}
