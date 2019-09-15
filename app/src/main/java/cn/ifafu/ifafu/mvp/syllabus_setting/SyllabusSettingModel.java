package cn.ifafu.ifafu.mvp.syllabus_setting;

import android.content.Context;

import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;

public class SyllabusSettingModel extends BaseZFModel implements SyllabusSettingContract.Model {

    public SyllabusSettingModel(Context context) {
        super(context);
    }

    @Override
    public SyllabusSetting getSetting() {
        SyllabusSetting setting = repository.getSyllabusSetting();
        if (setting == null) {
            setting = new SyllabusSetting(repository.getLoginUser().getAccount());
            repository.saveSyllabusSetting(setting);
        }
        return setting;
    }

    @Override
    public void save(SyllabusSetting setting) {
        repository.saveSyllabusSetting(setting);
    }
}
