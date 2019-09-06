package cn.ifafu.ifafu.electricity.http;

import androidx.annotation.NonNull;

import java.io.IOException;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.electricity.util.CookieUtils;
import cn.ifafu.ifafu.util.SPUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CookieInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String cookie = CookieUtils.getCookie();
        if (!cookie.isEmpty()) {
            builder.addHeader("Cookie", cookie);
        }
        Response response = chain.proceed(builder.build());
        if (!response.headers("Set-Cookie").isEmpty()) {
            for (String header: response.headers("Set-Cookie")) {
                String[] kv = header.substring(0, header.indexOf(";")).split("=");
                SPUtils.get(Constant.SP_ELEC).putString(kv[0], kv[1]);
            }
        }
        return response;
    }
}
