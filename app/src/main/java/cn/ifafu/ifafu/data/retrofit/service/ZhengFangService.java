package cn.ifafu.ifafu.data.retrofit.service;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ZhengFangService {

    @GET
    Call<ResponseBody> get(@Url String url);

    @GET
    Call<ResponseBody> get(@Url String url, @Header("Referer") String referer);

    @POST
    @FormUrlEncoded
    Call<ResponseBody> post(
            @Url String url,
            @Header("Referer") String referer,
            @FieldMap Map<String, String> fieldMap
    );

    @POST
    @FormUrlEncoded
    Call<ResponseBody> login(
            @Url String url,
            @FieldMap(encoded = true) Map<String, String> filedMap);
}
