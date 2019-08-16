package cn.ifafu.ifafu.data.http.service;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ZhengFangService {

    @GET
    Observable<ResponseBody> getCaptcha(@Url String url);

    @GET
    Observable<ResponseBody> base(@Url String url);

    @GET
    Observable<ResponseBody> base(@Url String url, @Header("Referer") String referer);

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> login(
            @Url String url,
            @FieldMap Map<String, String> filedMap);

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> getInfo(
            @Url String url,
            @Header("Referer") String referer,
            @FieldMap Map<String, String> fieldMap
    );
}
