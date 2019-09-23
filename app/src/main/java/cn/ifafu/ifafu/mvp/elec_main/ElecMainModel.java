package cn.ifafu.ifafu.mvp.elec_main;

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

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.entity.ElecCookie;
import cn.ifafu.ifafu.data.entity.ElecQuery;
import cn.ifafu.ifafu.data.entity.ElecUser;
import cn.ifafu.ifafu.data.entity.Selection;
import cn.ifafu.ifafu.data.http.elec.MainService;
import cn.ifafu.ifafu.data.http.elec.RetrofitFactory;
import cn.ifafu.ifafu.base.BaseModel;
import cn.ifafu.ifafu.util.SPUtils;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class ElecMainModel extends BaseModel implements ElecMainContract.Model {

    private final MainService service = RetrofitFactory.obtainService(MainService.class, null);

    private ElecUser elecUser;

    ElecMainModel(Context context) {
        super(context);
        elecUser = repository.getElecUser();
    }

    @Override
    public Observable<Boolean> initCookie() {
        return Observable.fromCallable(() -> {
            ElecCookie elecCookie = repository.getElecCookie();
            service.default2(elecCookie.getRescouseType()).execute();
            return service.page(
                    "31", "3", "2", "", "electricity",
                    URLEncoder.encode("交电费", "gbk"),
                    elecCookie.get("sourcetypeticket"),
                    SPUtils.get(Constant.SP_ELEC).getString("IMEI"),
                    "0", "1"
            ).execute().body().string().contains("<title>登录</title>");
        });
    }

    @Override
    public String getXfbAccount() {
        return elecUser.getXfbAccount();
    }

    public Observable<String> queryElectricity(ElecQuery data) {
        return Observable.fromCallable(() -> {
            ResponseBody responseBody = service.query(
                    data.toFiledMap(ElecQuery.Query.ROOMINFO)
            ).execute().body();
            return JSONObject.parseObject(responseBody.string())
                    .getJSONObject("Msg")
                    .getJSONObject("query_elec_roominfo")
                    .getString("errmsg");
        });
    }

    @Override
    public Observable<String> elecPay(ElecQuery data, String price) {
        return Observable.fromCallable(() -> {
            ResponseBody responseBody = service.elecPay(
                    "http://cardapp.fafu.edu.cn:8088/PPage/ComePage",
                    "###", "1", data.getAid(),
                    data.getXfbId(), price, data.getRoom(), data.getRoom(),
                    data.getFloorId(), data.getFloor(),
                    data.getBuildingId(), data.getBuilding(),
                    data.getAreaId(), data.getArea(), "true"
            ).execute().body();
            JSONObject jo = JSONObject.parseObject(responseBody.string())
                    .getJSONObject("Msg");

            String msg;
            if (jo.containsKey("pay_elec_gdc")) {
                return jo.getJSONObject("pay_elec_gdc")
                        .getString("errmsg");
            } else {
                return jo.toJSONString();
            }
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
    public void save(ElecQuery data) {
        repository.saveElecQuery(data);
    }

    @Override
    public ElecQuery getQueryData() {
        return repository.getElecQuery();
    }
}
