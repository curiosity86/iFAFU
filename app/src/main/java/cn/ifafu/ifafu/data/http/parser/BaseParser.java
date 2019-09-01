package cn.ifafu.ifafu.data.http.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ifafu.ifafu.data.exception.NoLogException;
import io.reactivex.ObservableTransformer;
import okhttp3.ResponseBody;

public abstract class BaseParser<T> implements ObservableTransformer<ResponseBody, T> {

    protected Map<String, String> getHiddenParams(String html) throws Exception {
        Pattern p = Pattern.compile("alert\\('.*'\\);");
        Matcher m = p.matcher(html);
        if (m.find()) {
            String s = m.group();
            if (s.contains("不能查询")) {
                throw new NoLogException(s.substring(7, s.length() - 3));
            }
        }
        Map<String, String> params = new HashMap<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("input[type=\"hidden\"]");
        for (Element element : elements) {
            params.put(element.attr("name"), element.attr("value"));
        }
        return params;
    }

    protected String getAccount(Document document) {
        Elements e = document.select("span[id=\"Label5\"]");
        return e.text().replace("学号：", "");
    }
}
