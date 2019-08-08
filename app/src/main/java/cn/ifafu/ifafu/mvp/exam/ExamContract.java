package cn.ifafu.ifafu.mvp.exam;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;

class ExamContract {

    interface Presenter extends IPresenter {

    }

    interface View extends IView {
        void setSubtitle(String subtitle);

        void showEmptyView();

        void setExamAdapterData(List<Exam> data);
    }

    interface Model extends IModel {

    }
}
