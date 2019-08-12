package cn.ifafu.ifafu.data.http.service;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ZhengFangService {

    @GET
    Observable<ResponseBody> mainHtml(@Url String url);

    @GET
    Observable<ResponseBody> getCaptcha(@Url String url);

    @GET
    Observable<ResponseBody> loginBase(@Url String url);

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> login(
            @Url String url,
            @FieldMap Map<String, String> filedMap);

    @POST
    Observable<ResponseBody> getInfo(
            @Url String url,
            @Header("Referer") String referer
    );
}
