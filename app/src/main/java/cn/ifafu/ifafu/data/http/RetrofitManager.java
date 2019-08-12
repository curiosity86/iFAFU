package cn.ifafu.ifafu.data.http;

import java.util.ArrayList;
import java.util.List;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.http.service.ZhengFangService;
import cn.ifafu.ifafu.util.SPUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RetrofitManager {

    private static ZhengFangService service;

    private static Retrofit retrofit;

    private static List<String> cookieList = new ArrayList<>();

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Response response = chain.proceed(chain.request());
                    String cookieString = response.header("Set-Cookie");
                    if (cookieString != null) {
                        String[] cookies = cookieString.split(";");
                        for (int i = 0; i < cookies.length - 1 ; i++) {
                            if (!cookies[i].isEmpty()) {
                                cookieList.add(cookies[i]);
                            }
                        }
                    }
                    return response;
                })
                .addInterceptor(chain -> {
                    Request request;
                    if (cookieList.size() > 0) {
                        SPUtils.get(Constant.SP_COOKIE).putString("ASP.NET_SessionId", cookieList.get(0));
                        request = chain.request().newBuilder()
                                .addHeader("Cookie", cookieList.get(0))
                                .build();
                    } else {
                        request = chain.request();
                    }
                    return chain.proceed(request);
                })
                .build();
    }

    public static <T> T obtainService(Class<T> clazz) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.IFAFU_BASE_URL)
                    .client(getOkHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                    .build();
        }
        return retrofit.create(clazz);
    }

}
