package cn.ifafu.ifafu.mvp.web;

import cn.ifafu.ifafu.mvp.base.IZFModel;
import cn.ifafu.ifafu.mvp.base.IZFPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;

class WebContract {
    interface Presenter extends IZFPresenter {

    }

    interface View extends IView {
        void loadUrl(String url);

        void setTitle(String title);
    }

    interface Model extends IZFModel {
        /**
         * 获取首页Url
         *
         * @return 首页Url
         */
        String getMainUrl();
    }

}
