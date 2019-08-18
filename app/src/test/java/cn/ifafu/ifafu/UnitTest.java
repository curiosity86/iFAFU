package cn.ifafu.ifafu;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import cn.ifafu.ifafu.data.entity.QueryApi;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.data.entity.ZFUrl;
import cn.ifafu.ifafu.data.http.parser.ScoreParser;

public class UnitTest {

    @Test
    public void test() throws ParseException {
        System.out.println("1230000000".replaceAll("0+?$", "").replaceAll("[.]$", ""));
    }


}
