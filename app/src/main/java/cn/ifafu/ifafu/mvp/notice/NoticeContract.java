package cn.ifafu.ifafu.mvp.notice;

import java.util.Collection;
import java.util.List;

import cn.ifafu.ifafu.data.entity.Notice;
import cn.woolsen.android.mvp.i.IModel;
import cn.woolsen.android.mvp.i.IPresenter;
import cn.woolsen.android.mvp.i.IView;

class NoticeContract {

    interface Presenter extends IPresenter {

    }

    interface View extends IView {

        void showNotices(List<Notice> notices);

    }

    interface Model extends IModel {

    }
}
