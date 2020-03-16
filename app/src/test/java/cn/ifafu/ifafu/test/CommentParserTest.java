package cn.ifafu.ifafu.test;

import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

import cn.ifafu.ifafu.FileUtils;
import cn.ifafu.ifafu.data.retrofit.parser.CommentParser;

public class CommentParserTest {

    private static final String path = "D:\\AndroidProjects\\iFAFU\\app\\src\\test\\java\\cn\\ifafu\\ifafu\\data\\comment\\bb";

    @Test
    public void test() throws Exception {
        CommentParser parser = new CommentParser();
        parser.parse(FileUtils.read(path + "\\001.html")).getData().forEach(item -> {
            System.out.println(JSONObject.toJSONString(item));
        });
    }

}
