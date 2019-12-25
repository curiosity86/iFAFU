package cn.ifafu.ifafu.data.http.parser;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ifafu.ifafu.entity.Response;
import cn.ifafu.ifafu.data.exception.VerifyException;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import okhttp3.ResponseBody;

public class LoginParser extends BaseParser<Response<String>> {

    /**
     * @param html 网页信息
     * @return {@link Response#SUCCESS} 登录成功 body = user only with name
     * {@link Response#FAILURE} 信息错误 msg = return msg
     * {@link Response#ERROR}   服务器错误  msg = error msg
     */
    public Response<String> parse(@NotNull String html) throws VerifyException {
        Document doc = Jsoup.parse(html);
        Element ele = doc.getElementById("xhxm");
        if (ele != null) {
            String name = ele.text().replace("同学", "");
            return Response.success(name);
        } else if (html.contains("输入原密码")) {
            return Response.success("佚名");
        }
        Elements script = doc.select("script[language=javascript]");
        if (script.size() < 2) {
            if (html.contains("ERROR")) {
                return Response.error("教务系统又双叒崩溃了！");
            }
            return Response.error("网络异常 0x001");
        } else {
            String s = getAlertString(script.get(1).html());
            if (s.contains("用户名") || s.contains("密码")) {
                return Response.failure(s);
            } else if (doc.text().contains("ERROR")) {
                return Response.error("教务系统又双叒崩溃了！");
            } else if (s.contains("验证码")) {
                throw new VerifyException();
            } else {
                return Response.error("网络异常 0x002");
            }
        }
    }

    @NotNull
    @Override
    public ObservableSource<Response<String>> apply(Observable<ResponseBody> upstream) {
        return upstream.map(responseBody -> parse(responseBody.string()));
    }

    private String getAlertString(String text) {
        Pattern p = Pattern.compile("alert\\('.*'\\);");
        Matcher m = p.matcher(text);
        if (m.find()) {
            String s = m.group();
            return s.substring(7, s.length() - 3);
        }
        return "";
    }
}
