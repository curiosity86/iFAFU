package cn.ifafu.ifafu.data.http.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.exception.VerifyException;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import okhttp3.ResponseBody;

public class LoginParser extends BaseParser<Response<String>> {

    /**
     * @param html 网页信息
     * @return {@link Response#SUCCESS} 登录成功 body = user only with name
     *         {@link Response#FAILURE} 信息错误 msg = return msg
     *         {@link Response#ERROR}   服务器错误  msg = error msg
     */
    private Response<String> parse(String html) throws VerifyException {
        Document doc = Jsoup.parse(html);
        Element ele = doc.getElementById("xhxm");
        if (ele != null) {
            String name = ele.text().replace("同学", "");
            return Response.success(name);
        }
        Elements script = doc.select("script[language=javascript]");
        if (script.size() < 2) {
            return Response.error("登录：网络异常");
        } else {
            String s = getAlertString(script.get(1).html());
            if (s.contains("用户名") || s.contains("密码")) {
                return Response.failure(s);
            } else if (doc.text().contains("ERROR")) {
                return Response.error("教务系统又双叒崩溃了");
            } else if (s.contains("验证码")) {
                throw new VerifyException();
            } else {
                return Response.error("登录：网络异常");
            }
        }
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

    @Override
    public ObservableSource<Response<String>> apply(Observable<ResponseBody> upstream) {
        return upstream.map(responseBody -> {
            String html = responseBody.string();
            Response<String> response = parse(html);
            response.setHiddenParams(getHiddenParams(html));
            return response;
        });
    }
}
