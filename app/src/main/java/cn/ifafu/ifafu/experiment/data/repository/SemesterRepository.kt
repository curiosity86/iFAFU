package cn.ifafu.ifafu.experiment.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import cn.ifafu.ifafu.data.bean.Semester
import cn.ifafu.ifafu.experiment.data.UserManager
import kotlinx.coroutines.Dispatchers
import java.util.*

class SemesterRepository(private val userManager: UserManager) {

    fun loadSemester(): LiveData<Semester> {
        return userManager.userSwitchMap { cert ->
            liveData(Dispatchers.IO) {
                val c = Calendar.getInstance()
                val termIndex = if (c[Calendar.MONTH] < 1 || c[Calendar.MONTH] > 6) 0 else 1
                c.add(Calendar.MONTH, 5)
                val toYear = c[Calendar.YEAR]
                val enrollmentYear = cert.account.run {
                    if (length == 10) {
                        substring(1, 3).toInt() + 2000
                    } else {
                        substring(0, 2).toInt() + 2000
                    }
                }
                val yearList = arrayListOf("全部")
                for (i in enrollmentYear until toYear) {
                    yearList.add(0, String.format(Locale.CHINA, "%d-%d", i, i + 1))
                }
                val semester = Semester(yearList, listOf("1", "2", "全部"), 0, termIndex)
                emit(semester)
            }
        }
    }
}