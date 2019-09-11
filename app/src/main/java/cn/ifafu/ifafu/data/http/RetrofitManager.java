package cn.ifafu.ifafu.data.http;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.util.SPUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RetrofitManager {

    private static Retrofit retrofit;

    private static final int CONNECT_TIME_OUT = 15;//连接超时时长x秒
    private static final int READ_TIME_OUT = 15;//读数据超时时长x秒
    private static final int WRITE_TIME_OUT = 15;//写数据接超时时长x秒

    private static List<String> cookieList = new ArrayList<>();

    static {
        Map<String, String> cookieMap = (Map<String, String>) SPUtils.get(Constant.SP_COOKIE).getAll();
        cookieList.addAll(cookieMap.values());
    }

    private RetrofitManager() {
    }

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(cookieInterceptor())
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .build();
    }

    static <T> T obtainService(Class<T> clazz) {
        if (retrofit == null) {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.IFAFU_BASE_URL)
                    .client(getOkHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                    .build();
        }
        return retrofit.create(clazz);
    }

    private static Interceptor cookieInterceptor() {
        return chain -> {
            Request request;
            if (cookieList.size() > 0) {
                SPUtils.get(Constant.SP_COOKIE).putString("ASP.NET_SessionId", cookieList.get(0));
                request = chain.request().newBuilder()
                        .addHeader("Cookie", cookieList.get(0))
                        .build();
            } else {
                request = chain.request();
            }
            Response response = chain.proceed(request);
            String cookieString = response.header("Set-Cookie");
            if (cookieString != null) {
                String[] cookies = cookieString.split(";");
                for (int i = 0; i < cookies.length - 1; i++) {
                    if (!cookies[i].isEmpty()) {
                        cookieList.add(cookies[i]);
                    }
                }
            }
            return response;
        };
    }

}
