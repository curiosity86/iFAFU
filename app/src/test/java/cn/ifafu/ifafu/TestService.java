package cn.ifafu.ifafu;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface TestService {
    @GET
    Observable<ResponseBody> byUrl(@Url String url);

    @GET
    Observable<ResponseBody> byUrl(@Url String url, @Header("referer") String referer);

    @GET("/{end}")
    Observable<ResponseBody> byPath(@Path(value = "end", encoded = true) String path);
}
