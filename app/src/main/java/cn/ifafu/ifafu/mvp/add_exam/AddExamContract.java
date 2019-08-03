package cn.ifafu.ifafu.mvp.add_exam;

import cn.ifafu.ifafu.data.entity.Exam;
import cn.woolsen.android.mvp.i.IModel;
import cn.woolsen.android.mvp.i.IPresenter;
import cn.woolsen.android.mvp.i.IView;

class AddExamContract {

    interface Presenter extends IPresenter {
        void onSave();
    }

    interface View extends IView {

        String getNameText();

        String getAddressText();

        String getSeatText();

        String getDateText();

    }

    interface Model extends IModel {
        void save(Exam exam);
    }

}
