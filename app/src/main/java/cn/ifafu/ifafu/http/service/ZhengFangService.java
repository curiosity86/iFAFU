package cn.ifafu.ifafu.http.service;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ZhengFangService {

    @GET("xs_main.aspx")
    Observable<ResponseBody> defaultHtml(@Field("xh") String xh);

    @GET("CheckCode.aspx")
    Call<ResponseBody> getCaptcha();

    @POST("default2.aspx")
    @FormUrlEncoded
    Observable<ResponseBody> login(
            @Field("txtUserName") String xh,
            @Field("Textbox2") String pwd,
            @Field("txtSecretCode") String captcha,
            @Field("__VIEWSTATE") String viewState,
            @Field("__VIEWSTATEGENERATOR") String viewStateGenerator,
            @Field("Button1") String button1,
            @Field("RadioButtonList1") String rbl);

    @POST("{api}")
    Observable<ResponseBody> getInfo(
            @Path("api") String api,
            @Header("Referer") String referer,
            @Query("xh") String xh,
            @Query(value = "xm") String xm,
            @Query("gnmkdm") String gnmkdm
    );
}
