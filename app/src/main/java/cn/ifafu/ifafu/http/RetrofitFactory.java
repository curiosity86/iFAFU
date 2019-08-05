package cn.ifafu.ifafu.http;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RetrofitFactory {

    private static Map<String, Retrofit> retrofitMap = new HashMap<>();

    public static <T> T obtainService(Class<T> clazz, String baseUrl) {
        return getRetrofit(baseUrl, false).create(clazz);
    }

    public static <T> T obtainServiceTemp(Class<T> clazz, String baseUrl) {
        return getRetrofit(baseUrl, true).create(clazz);
    }

    private static Retrofit getRetrofit(String baseUrl, boolean temp) {
        if (retrofitMap.containsKey(baseUrl)) {
            return retrofitMap.get(baseUrl);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
        if (!temp) {
            retrofitMap.put(baseUrl, retrofit);
        }
        return retrofit;
    }

}
