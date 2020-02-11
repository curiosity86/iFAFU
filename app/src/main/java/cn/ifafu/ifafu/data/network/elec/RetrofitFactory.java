package cn.ifafu.ifafu.data.network.elec;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class RetrofitFactory {

    private static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new HeaderInterceptor())
            .addInterceptor(new CookieInterceptor())
            .build();

    private static Retrofit retrofit = new Retrofit.Builder()
            .client(client)
            .baseUrl("http://cardapp.fafu.edu.cn:8088")
            .build();

    public static <T> T obtainService(Class<T> clazz) {
        return retrofit.create(clazz);
    }

}
