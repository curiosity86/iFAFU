package cn.ifafu.ifafu.data.http.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.ObservableTransformer;
import okhttp3.ResponseBody;

public abstract class BaseParser<T> implements ObservableTransformer<ResponseBody, T> {

    protected Map<String, String> getHiddenParams(String html) {
        Map<String, String> params = new HashMap<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("input[type=\"hidden\"]");
        for (Element element : elements) {
            params.put(element.attr("name"), element.attr("value"));
        }
        return params;
    }
}
