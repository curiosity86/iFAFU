package cn.ifafu.ifafu.mvp.syllabus_setting;

import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;

public class SyllabusSettingContract {

    interface View extends IView {

        void initRecycleView(SyllabusSetting setting);

    }

    public interface Presenter extends IPresenter {

    }

    interface Model extends IModel {
        SyllabusSetting getSetting();
    }

}
