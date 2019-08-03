package cn.ifafu.ifafu;

import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import cn.woolsen.android.uitl.DateUtils;

public class ExampleUnitTest {

    @Test
    public void test() {
        System.out.println(Arrays.toString(DateUtils.getWeekDates(new Date(1567267200000L), 0, Calendar.SUNDAY, "MM-dd")));
    }


}
