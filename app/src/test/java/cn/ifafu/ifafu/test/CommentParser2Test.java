package cn.ifafu.ifafu.test;

import org.junit.Test;

import cn.ifafu.ifafu.FileUtils;
import cn.ifafu.ifafu.data.retrofit.parser.CommentParser2;

public class CommentParser2Test {

    private static final String path = "D:\\AndroidProjects\\iFAFU\\app\\src\\test\\java\\cn\\ifafu\\ifafu\\data\\comment";

    @Test
    public void test() throws Exception {
        CommentParser2 parser = new CommentParser2();
        parser.parse(FileUtils.read(path + "\\0001.html")).entrySet().forEach(e -> {
            System.out.println(e.toString());
        });
    }

}
