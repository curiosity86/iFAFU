package cn.ifafu.ifafu.data.http.parser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.ifafu.ifafu.data.exception.NoAuthException;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import okhttp3.ResponseBody;

public abstract class BaseParser<T> implements ObservableTransformer<ResponseBody, T> {

    abstract T parse(String html) throws Exception;

    protected String getAccount(Document document) {
        Elements e = document.select("span[id=\"Label5\"]");
        return e.text().replace("学号：", "");
    }

    @Override
    public ObservableSource<T> apply(Observable<ResponseBody> upstream) {
        return upstream.map(responseBody -> {
            String html = responseBody.string();
            if (html.matches("请登录|请重新登陆|302 Found")) {
                throw new NoAuthException();
            }
            return parse(html);
        });
    }
}
