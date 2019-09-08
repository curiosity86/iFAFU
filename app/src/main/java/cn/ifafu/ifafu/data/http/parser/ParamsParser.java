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

public class ParamsParser extends BaseParser<Map<String, String>>{

    @Override
    Map<String, String> parse(String html) throws Exception {
        Pattern p = Pattern.compile("alert\\('.*'\\);");
        Matcher m = p.matcher(html);
        if (m.find()) {
            String s = m.group();
            if (s.matches("现在不能查询")) {
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

}
