package cn.ifafu.ifafu.mvp.syllabus_setting;

import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.mvp.base.BasePresenter;

public class SyllabusSettingPresenter extends BasePresenter<SyllabusSettingContract.View, SyllabusSettingContract.Model>
        implements SyllabusSettingContract.Presenter {

    public SyllabusSettingPresenter(SyllabusSettingContract.View view) {
        super(view, new SyllabusSettingModel(view.getContext()));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mView.initRecycleView(mModel.getSetting());
    }

    @Override
    public void save(SyllabusSetting setting) {
        mModel.save(setting);
    }
}
