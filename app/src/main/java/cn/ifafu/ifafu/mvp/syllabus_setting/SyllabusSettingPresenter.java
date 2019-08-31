package cn.ifafu.ifafu.mvp.syllabus_setting;

import cn.ifafu.ifafu.mvp.base.BasePresenter;

public class SyllabusSettingPresenter extends BasePresenter<SyllabusSettingContract.View, SyllabusSettingContract.Model>
        implements SyllabusSettingContract.Presenter {

    public SyllabusSettingPresenter(SyllabusSettingContract.View view) {
        super(view);
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
