package cn.ifafu.ifafu.mvp.web;

import android.content.Context;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.data.entity.ZFUrl;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class WebModel extends BaseZFModel implements WebContract.Model {

    WebModel(Context context) {
        super(context);
    }

    @Override
    public String getMainUrl() {
        return School.getUrl(ZFUrl.MAIN, getUser());
    }

    @Override
    public Observable<String> getMainHtml() {
        return zhengFang.mainHtml(getMainUrl())
                .map(ResponseBody::string);
    }
}
