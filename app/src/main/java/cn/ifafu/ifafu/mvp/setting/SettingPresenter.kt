package cn.ifafu.ifafu.mvp.setting

import android.app.Activity
import cn.ifafu.ifafu.base.BasePresenter
import cn.ifafu.ifafu.entity.GlobalSetting
import cn.ifafu.ifafu.view.adapter.syllabus_setting.CheckBoxItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingPresenter(view: SettingContract.View)
    : BasePresenter<SettingContract.View, SettingContract.Model>(view, SettingModel(view.context)),
        SettingContract.Presenter {

    private lateinit var setting: GlobalSetting

    private var settingHash = 0

    override fun onCreate() {
        GlobalScope.launch(Dispatchers.IO) {
            setting = mModel.getSetting()
            settingHash = setting.hashCode()
            launch(Dispatchers.Main) {
                mView.initRecycleView(listOf(CheckBoxItem("旧版主页主题", "应需求而来，喜欢0.9版本iFAFU界面就快来呀", setting.theme == GlobalSetting.THEME_OLD) {
                    setting.theme = if (it) {
                        GlobalSetting.THEME_OLD
                    } else {
                        GlobalSetting.THEME_NEW
                    }
                    GlobalScope.launch(Dispatchers.IO) {
                        mModel.save(setting)
                    }
                }))
            }
        }
    }

    override fun onFinish() {
        if (settingHash != setting.hashCode()) {
            mView.activity.setResult(Activity.RESULT_OK)
        }
    }

}
