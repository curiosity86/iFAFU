package cn.ifafu.ifafu.electricity.http;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    public static <T> T obtainService(Class<T> clazz, Map<String, String> headers) {
        if (headers != null) {
            client.newBuilder()
                    .addInterceptor(chain -> {
                        Request.Builder builder = chain.request().newBuilder();
                        for (Map.Entry<String, String> entry : headers.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            builder.addHeader(key, value);
                        }
                        return chain.proceed(builder.build());
                    });
        }
        return retrofit.create(clazz);
    }

}
