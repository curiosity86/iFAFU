package cn.ifafu.ifafu.test;

import cn.ifafu.ifafu.FileUtils;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.http.parser.SyllabusParser;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.util.*;

@RunWith(Parameterized.class)
public class SyllabusParserTest {

    private String in;
    private Object out;
    private SyllabusParser parser;

    //按实际项目路径地址修改
    private static final String path = "D:\\AndroidProjects\\iFAFU\\app\\src\\test\\java\\cn\\ifafu\\ifafu\\data";

    public SyllabusParserTest(String in, Object out) {
        this.in = in;
        this.out = out;
    }

    @Before
    public void initialize() throws IOException {
        parser = new SyllabusParser();
    }


    @Test
    public void test() throws Exception {
//        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("name", "weekday", "teacher", "address", "weekSet", "beginNode", "nodeLength");
//        System.out.println(JSONObject.toJSONString(actual, filter));
        String html = FileUtils.read(path + "\\in\\" + in, "gb2312");
        List<Course> actual = parser.parse(html);
        String json = FileUtils.read(path + "\\out\\" + out);
        List<Course> except = JSONObject.parseArray(json, Course.class);
        System.out.println("except:" + except);
        System.out.println("actual:" + actual);
        Assert.assertArrayEquals(actual.toArray(), except.toArray());
    }

    @Parameterized.Parameters
    public static Collection data() throws IOException {
        return Arrays.asList(new Object[][] {
                {"000.html", "000.json"},
                {"001.html", "001.json"},
                {"002.html", "002.json"},
                {"003.html", "003.json"},
        });
    }

}
