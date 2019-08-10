package cn.ifafu.ifafu.mvp.web;

import android.content.Context;

import cn.ifafu.ifafu.mvp.base.BaseZFModel;

public class WebModel extends BaseZFModel implements WebContract.Model {

    WebModel(Context context) {
        super(context);
    }

    @Override
    public String getMainUrl() {
        return getReferer(getMUser());
    }
}
