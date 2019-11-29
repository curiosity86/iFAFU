package cn.ifafu.ifafu;

import java.io.*;

public class FileUtils {

    public static String read(String filePath) throws IOException {
        return read(filePath, "gb2312");
    }

    public static String read(String filePath, String charset) throws IOException {
        InputStream is = new FileInputStream(filePath);//文件读取
        InputStreamReader isr;//解码
        isr = new InputStreamReader(is, charset);
        int c;
        StringBuilder sb = new StringBuilder();
        while ((c = isr.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    public static String[] list(String dirPath) {
        File dir = new File(dirPath);
        return dir.list();
    }
}
