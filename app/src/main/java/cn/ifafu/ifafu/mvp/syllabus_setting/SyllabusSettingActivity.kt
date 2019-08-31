package cn.ifafu.ifafu.mvp.syllabus_setting

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.mvp.base.BaseActivity
import cn.ifafu.ifafu.view.adapter.syllabus_setting.CheckBoxBinder
import cn.ifafu.ifafu.view.adapter.syllabus_setting.CheckBoxItem
import cn.ifafu.ifafu.view.adapter.syllabus_setting.SeekBarBinder
import cn.ifafu.ifafu.view.adapter.syllabus_setting.SeekBarItem
import kotlinx.android.synthetic.main.activity_syllabus_setting.*
import me.drakeet.multitype.MultiTypeAdapter

class SyllabusSettingActivity : BaseActivity<SyllabusSettingContract.Presenter>(), SyllabusSettingContract.View {

    private val idTotalNode = 0
    private val idTotalWeek = 1
    private val idShowSaturday = 2
    private val idShowSunday = 3

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_syllabus_setting
    }

    override fun initData(savedInstanceState: Bundle?) {
        tb_syllabus_setting.setNavigationOnClickListener { finish() }
    }

    override fun initRecycleView(setting: SyllabusSetting) {
        val adapter = MultiTypeAdapter()
        adapter.register(SeekBarItem::class, SeekBarBinder {
            seekBarItem, i -> onSeekBarChange(seekBarItem.id, i)
        })
        adapter.register(CheckBoxItem::class, CheckBoxBinder {
            checkBoxItem, b ->  onCheckChange(checkBoxItem.id, b)
        })
        adapter.items = listOf(
                SeekBarItem(idTotalNode, "一天课程的节数", setting.nodeCnt, "节", 8, 20),
                SeekBarItem(idTotalWeek, "总共周数", setting.weekCnt, "周", 18, 30),
                CheckBoxItem(idShowSaturday, "显示周六", setting.showSaturday),
                CheckBoxItem(idShowSunday, "显示周日", setting.showSunday)
        )
        rv_syllabus_setting.layoutManager = LinearLayoutManager(this)
        rv_syllabus_setting.adapter = adapter
    }

    private fun onSeekBarChange(id: Int, value: Int) {
        when(id) {
            idTotalNode-> {

            }
            idTotalWeek -> {

            }
        }
    }

    private fun onCheckChange(id: Int, value: Boolean) {
        when(id) {
            idShowSaturday -> {

            }
            idShowSunday -> {

            }
        }
    }
}
