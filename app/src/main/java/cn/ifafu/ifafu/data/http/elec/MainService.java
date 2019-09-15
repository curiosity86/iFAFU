package cn.ifafu.ifafu.data.http.elec;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MainService {


    /**
     * 初始化Cookie
     */
    @POST("/NoBase/TPGetAppList")
    @FormUrlEncoded
    Call<ResponseBody> default2(
            @Field("sourcetype") String sourcetype
    );

    /**
     * 获取交电费页面，更新Cookie
     */
    @POST("/Page/Page")
    @FormUrlEncoded
    Call<ResponseBody> page(
            @Field("flowid") String flowid,
            @Field("type") String type,
            @Field("apptype") String apptype,
            @Field("url") String url,
            @Field("EMenuName") String EMenuName,
            @Field("MenuName") String MenuName,
            @Field("sourcetype") String sourcetype,
            @Field("IMEI") String IMEI,
            @Field("language") String language,
            @Field("comeapp") String comeapp
    );


    /**
     * 查询余额
     */
    @POST("/User/GetCardInfoByAccountNoParm")
    @FormUrlEncoded
    Call<ResponseBody> queryBalance(
            @Field("json") String json
    );

    /**
     * 查询电费信息
     */
    @POST("/Tsm/TsmCommon")
    @FormUrlEncoded
    Call<ResponseBody> query(
            @Field("jsondata") String jsondata,
            @Field("funname") String funname,
            @Field("json") String json
    );

    /**
     * 查询接口
     */
    @POST("/Tsm/TsmCommon")
    @FormUrlEncoded
    Call<ResponseBody> query(
            @FieldMap Map<String, String> map
    );

    /**
     * 充值电费
     */
    @POST("/Tsm/Elec_Pay")
    @FormUrlEncoded
    Call<ResponseBody> elecPay(
            @Header("Referer") String referer,
            @Field("acctype") String acceptype,
            @Field("paytype") String paytype,
            @Field("aid") String aid,
            @Field("account") String account,
            @Field("tran") String tran,
            @Field("roomid") String roomid,
            @Field("room") String room,
            @Field("floorid") String floorid,
            @Field("floor") String floor,
            @Field("buildingid") String buildingid,
            @Field("building") String building,
            @Field("areaid") String areaid,
            @Field("areaname") String areaname,
            @Field("json") String json
    );

}
