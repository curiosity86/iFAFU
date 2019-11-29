package cn.ifafu.ifafu.test;

import org.junit.Test;

import cn.ifafu.ifafu.FileUtils;
import cn.ifafu.ifafu.data.http.parser.CommentParser2;
import cn.ifafu.ifafu.data.http.parser.CommentParserJS2;

public class CommentParserJS2Test {

    private static final String path = "D:\\AndroidProjects\\iFAFU\\app\\src\\test\\java\\cn\\ifafu\\ifafu\\data\\comment";

    @Test
    public void test() throws Exception {
        CommentParserJS2 parser = new CommentParserJS2();
        parser.parse(FileUtils.read(path + "\\1001.html")).entrySet().forEach(e -> {
            System.out.println(e.toString());
        });
    }

}
