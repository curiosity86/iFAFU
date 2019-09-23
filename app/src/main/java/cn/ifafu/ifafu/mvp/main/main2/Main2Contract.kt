package cn.ifafu.ifafu.mvp.main.main2

import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.NextCourse2
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.data.entity.YearTerm
import cn.ifafu.ifafu.mvp.main.BaseMainContract
import io.reactivex.Observable

class Main2Contract {
    interface View : BaseMainContract.View {

        fun setAccountText(account: String)

        fun setNameText(name: String)

        fun makeLeftMenu(data: Map<String, List<Pair<String, Int>>>)

        fun setOnlineStatus(online: Boolean)

        fun setYearTermTitle(title: String)

        fun setWeatherText(text: String)

        fun setSyllabusTime(text: String)

        fun setNextCourse(nextCourse: NextCourse2)
    }

    interface Presenter: BaseMainContract.Presenter {
        fun updateWeather()

        fun updateNextCourse()
    }

    interface Model: BaseMainContract.Model {

        fun getSyllabusSetting(): SyllabusSetting

        fun getFunctionTab(): Map<String, List<Pair<String, Int>>>

        fun getYearTermList(): Observable<YearTerm>

        fun getYearTerm(): Pair<String, String>

        fun getNextCourse2(courses: List<Course>): NextCourse2
    }
}