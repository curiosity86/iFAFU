package cn.ifafu.ifafu.mvp.exam;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Exam;
import cn.woolsen.android.mvp.i.IModel;
import cn.woolsen.android.mvp.i.IPresenter;
import cn.woolsen.android.mvp.i.IView;

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
