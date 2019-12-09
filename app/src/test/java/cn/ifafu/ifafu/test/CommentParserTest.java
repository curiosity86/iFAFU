package cn.ifafu.ifafu.test;

import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

import java.io.IOException;
import java.util.function.Consumer;

import cn.ifafu.ifafu.FileUtils;
import cn.ifafu.ifafu.data.entity.CommentItem;
import cn.ifafu.ifafu.data.http.parser.CommentParser;

public class CommentParserTest {

    private static final String path = "D:\\AndroidProjects\\iFAFU\\app\\src\\test\\java\\cn\\ifafu\\ifafu\\data\\comment\\bb";

    @Test
    public void test() throws Exception {
        CommentParser parser = new CommentParser();
        parser.parse(FileUtils.read(path + "\\001.html")).getBody().forEach(item -> {
            System.out.println(JSONObject.toJSONString(item));
        });
    }

}
