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
        return repository.getSyllabusSetting();
    }
}
