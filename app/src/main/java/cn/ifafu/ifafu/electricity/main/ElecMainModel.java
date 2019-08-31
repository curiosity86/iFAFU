package cn.ifafu.ifafu.electricity.main;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.dao.QueryDataDao;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.electricity.data.QueryData;
import cn.ifafu.ifafu.electricity.data.Selection;
import cn.ifafu.ifafu.electricity.http.MainService;
import cn.ifafu.ifafu.electricity.http.RetrofitFactory;
import cn.ifafu.ifafu.electricity.util.SPUtils;
import cn.ifafu.ifafu.mvp.base.BaseModel;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class ElecMainModel extends BaseModel implements ElecMainContract.Model {

    private final MainService service = RetrofitFactory.obtainService(MainService.class, null);

    private final QueryDataDao queryDao;

    ElecMainModel(Context context) {
        super(context);
        queryDao = DaoManager.getInstance().getDaoSession().getQueryDataDao();
    }

    @Override
    public Observable<Boolean> initCookie() {
        return Observable.fromCallable(() -> {
            service.default2(SPUtils.get("Cookie").getString("RescouseType")).execute();
            return service.page(
                    "31", "3", "2", "", "electricity",
                    URLEncoder.encode("交电费", "gbk"),
                    SPUtils.get("Cookie").getString("sourcetypeticket"),
                    SPUtils.get("Const").getString("IMEI"),
                    "0", "1"
            ).execute().body().string().contains("<title>登录</title>");
        });
    }

    @Override
    public String getAccount() {
        return SPUtils.get("UserInfo").getString("account");
    }

    public Observable<String> queryElec(QueryData data) {
        return Observable.fromCallable(() -> {
            ResponseBody responseBody = service.query(
                    data.toFiledMap(QueryData.Query.ROOMINFO)
            ).execute().body();
            return JSONObject.parseObject(responseBody.string())
                    .getJSONObject("Msg")
                    .getJSONObject("query_elec_roominfo")
                    .getString("errmsg");
        });
    }

    @Override
    public Observable<String> elecPay(QueryData data, String price) {
        return Observable.create(emitter -> {
            ResponseBody responseBody = service.elecPay(
                    "http://cardapp.fafu.edu.cn:8088/PPage/ComePage",
                    "###", "1", data.getAid(),
                    data.getAccount(), price, data.getRoom(), data.getRoom(),
                    data.getFloorId(), data.getFloor(),
                    data.getBuildingId(), data.getBuilding(),
                    data.getAreaId(), data.getArea(), "true"
            ).execute().body();
            String msg = JSONObject.parseObject(responseBody.string())
                    .getJSONObject("Msg")
                    .getJSONObject("pay_elec_gdc")
                    .getString("errmsg");
            emitter.onNext(msg);
            emitter.onComplete();
        });
    }

    @Override
    public List<Selection> getSelectionFromJson() {
        try {
            InputStream is = mContext.getAssets().open("data.json");
            BufferedReader isr = new BufferedReader(new InputStreamReader(is));
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = isr.readLine()) != null) {
                sb.append(str);
            }
            return JSONObject.parseArray(sb.toString(), Selection.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Observable<Map<String, String>> queryDKInfos() {
        return Observable.create(emitter -> {
            ResponseBody responseBody = service.query(
                    "{\"query_applist\":{ \"apptype\": \"elec\" }}",
                    "synjones.onecard.query.applist",
                    "true"
            ).execute().body();
            JSONArray ctrlList = JSONObject.parseObject(responseBody.string())
                    .getJSONObject("Msg")
                    .getJSONObject("query_applist")
                    .getJSONArray("applist");
            Map<String, String> aidMap = new LinkedHashMap<>();
            for (Object o : ctrlList) {
                JSONObject jo = (JSONObject) o;
                aidMap.put(jo.getString("name"), jo.getString("aid"));
            }
            emitter.onNext(aidMap);
            emitter.onComplete();
        });
    }

    @Override
    public Observable<Double> queryBalance() {
        return Observable.create(emitter -> {
            ResponseBody responseBody = service.queryBalance(
                    "true"
            ).execute().body();
            JSONObject msg = JSONObject.parseObject(responseBody.string())
                    .getJSONObject("Msg")
                    .getJSONObject("query_card")
                    .getJSONArray("card")
                    .getJSONObject(0);
            Double total = (msg.getIntValue("db_balance") + msg.getIntValue("unsettle_amount")) / 100.0;
            emitter.onNext(total);
            emitter.onComplete();
        });
    }

    @Override
    public String getSno() {
        return SPUtils.get("UserInfo").getString("sno");
    }

    @Override
    public void clearAll() {
        SPUtils.get("UserInfo").clear();
        SPUtils.get("Cookie").clear();
    }

    @Override
    public void save(QueryData data) {
        queryDao.insertOrReplace(data);
    }

    @Override
    public QueryData getQueryData() {
        String account = SPUtils.get("UserInfo").getString("account");
        return queryDao.load(account);
    }
}
