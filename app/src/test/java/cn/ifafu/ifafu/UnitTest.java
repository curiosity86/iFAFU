package cn.ifafu.ifafu;

import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import cn.ifafu.ifafu.data.entity.QueryApi;
import cn.ifafu.ifafu.data.entity.ZFUrl;

public class UnitTest {

    @Test
    public void test() throws InterruptedException {

        Calendar c = Calendar.getInstance();
        System.out.println(c.get(Calendar.YEAR));
        c.add(Calendar.MONTH, 6);
        System.out.println(c.get(Calendar.YEAR));
        c.add(Calendar.MONTH, 6);
        System.out.println(c.get(Calendar.YEAR));
    }

    static class TestRunnable implements Runnable {

        static int index = 0;

        static void increase() {
            index++;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                increase();
            }
        }
    }
}
