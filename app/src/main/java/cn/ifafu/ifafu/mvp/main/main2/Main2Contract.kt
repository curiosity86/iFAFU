package cn.ifafu.ifafu.mvp.main.main2

import cn.ifafu.ifafu.data.entity.*
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

        fun setScoreText(text: String?)

        fun setExamData(data: List<NextExam>)
    }

    interface Presenter: BaseMainContract.Presenter {
        fun updateWeather()

        fun updateNextCourse()

        fun updateExamInfo()

        fun updateScoreInfo()
    }

    interface Model: BaseMainContract.Model {

        fun getSyllabusSetting(): SyllabusSetting

        fun getFunctionTab(): Map<String, List<Pair<String, Int>>>

        fun getYearTermList(): Observable<YearTerm>

        fun getYearTerm(): Pair<String, String>

        fun getNextCourse2(): Observable<NextCourse2>

        fun getNextExams(): Observable<List<NextExam>>

        fun getScore(): Observable<List<Score>>
    }
}