package cn.ifafu.ifafu.data.retrofit.service;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
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
    Observable<ResponseBody> initParams(@Url String url);

    @GET
    Observable<ResponseBody> initParams(@Url String url, @Header("Referer") String referer);

    @GET
    Call<ResponseBody> getCaptcha2(@Url String url);

    @GET
    Call<ResponseBody> initParams2(@Url String url);

    @GET
    Call<ResponseBody> initParams2(@Url String url, @Header("Referer") String referer);

    @GET
    Call<ResponseBody> getInfo2(
            @Url String url,
            @Header("Referer") String referer
    );

    @POST
    @FormUrlEncoded
    Call<ResponseBody> getInfo2(
            @Url String url,
            @Header("Referer") String referer,
            @FieldMap Map<String, String> fieldMap
    );

    @POST
    @FormUrlEncoded
    Call<ResponseBody> login2(
            @Url String url,
            @FieldMap(encoded = true) Map<String, String> filedMap);

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> login(
            @Url String url,
            @FieldMap(encoded = true) Map<String, String> filedMap);

    @POST
    Observable<ResponseBody> getInfo(
            @Url String url,
            @Header("Referer") String referer
    );

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> getInfo(
            @Url String url,
            @Header("Referer") String referer,
            @FieldMap Map<String, String> fieldMap
    );


    @POST
    @FormUrlEncoded
    Observable<ResponseBody> post(
            @Url String url,
            @FieldMap Map<String, String> fieldMap
    );

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> post(
            @Url String url,
            @Header("Referer") String referer,
            @FieldMap(encoded = true) Map<String, String> fieldMap
    );

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> parse(
            @Url String url,
            @Field(value = "html") String html
    );
}