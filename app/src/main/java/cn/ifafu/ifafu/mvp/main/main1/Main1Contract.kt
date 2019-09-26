package cn.ifafu.ifafu.mvp.main.main1

import android.graphics.drawable.Drawable
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.Holiday
import cn.ifafu.ifafu.data.entity.Menu
import cn.ifafu.ifafu.mvp.main.BaseMainContract
import cn.ifafu.ifafu.view.timeline.TimeAxis
import io.reactivex.Observable

class Main1Contract {

    interface View : BaseMainContract.View {

        fun setMenuAdapterData(menus: List<Menu>)

        fun setLeftMenuHeadIcon(headIcon: Drawable)

        fun setLeftMenuHeadName(name: String)

        fun setWeatherText(weather: Pair<String, String>)

        fun setCourseText(title: String, name: String, address: String, time: String)

        fun setTimeLineData(data: List<TimeAxis>)

    }

    interface Presenter : BaseMainContract.Presenter {

        fun shareApp()

        fun onRefresh()

        fun updateWeather()

        fun updateNextCourseView()

        fun updateTimeLine()

    }

    interface Model : BaseMainContract.Model {

        fun getThisTermExams(): List<Exam>

        /**
         * 获取主页菜单信息
         */
        fun getMenus(): Observable<List<Menu>>

        fun getSchoolIcon(): Drawable

        fun getHoliday(): List<Holiday>

        fun clearAllDate()
    }
}