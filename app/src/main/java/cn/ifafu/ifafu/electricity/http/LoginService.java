package cn.ifafu.ifafu.electricity.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginService {

    /**
     * 初始化Cookie
     */
    @GET("/Phone/Login")
    Call<ResponseBody> init(
            @Query("sourcetype") String sourcetype,
            @Query("IMEI") String IMEI,
            @Query("language") String language
    );

    @GET("/Phone/GetValidateCode")
    Call<ResponseBody> verify(
            @Query("time") String time
    );

    @POST("/Phone/Login")
    @FormUrlEncoded
    Call<ResponseBody> login(
            @Header("Referer") String referer,
            @Field("sno") String sno,
            @Field("pwd") String pwd,
            @Field("yzm") String yzm,
            @Field("remember") String remember,
            @Field("uclass") String uclass,
            @Field("zqcode") String zqcode,
            @Field("json") String json
    );
}
