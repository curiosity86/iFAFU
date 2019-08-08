package cn.ifafu.ifafu.mvp.add_exam;

import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;

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
