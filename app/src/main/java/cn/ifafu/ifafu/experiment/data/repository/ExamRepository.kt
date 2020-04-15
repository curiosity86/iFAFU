package cn.ifafu.ifafu.experiment.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import cn.ifafu.ifafu.data.bean.Semester
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.experiment.bean.Resource
import cn.ifafu.ifafu.experiment.data.UserManager
import cn.ifafu.ifafu.experiment.data.db.ExamDao
import cn.ifafu.ifafu.experiment.data.service.ZFService
import cn.ifafu.ifafu.experiment.util.toMediatorLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import java.util.*
import kotlin.collections.ArrayList

class ExamRepository(
        private val userManager: UserManager,
        private val examDao: ExamDao,
        private val zfService: ZFService
) {

    val semester: LiveData<Semester>
        get() = _semester
    private val _semester = loadSemester().toMediatorLiveData()

    private val coroutineScope: CoroutineScope = GlobalScope


    private var lock = false


    val examResource: LiveData<Resource<List<Exam>>>
        get() = _examResource
    private val _examResource = MediatorLiveData<Resource<List<Exam>>>().apply {

    }

    private fun loadSemester(): LiveData<Semester> {
        return userManager.userSwitchMap { user ->
            liveData(Dispatchers.IO) {
                val c = Calendar.getInstance()
                val termIndex = if (c[Calendar.MONTH] < 1 || c[Calendar.MONTH] > 6) 0 else 1
                c.add(Calendar.MONTH, 5)
                val toYear = c[Calendar.YEAR]
                val enrollmentYear = user.account.run {
                    if (length == 10) {
                        substring(1, 3).toInt() + 2000
                    } else {
                        substring(0, 2).toInt() + 2000
                    }
                }
                val yearList = ArrayList<String>()
                for (i in enrollmentYear until toYear) {
                    yearList.add(0, String.format(Locale.CHINA, "%d-%d", i, i + 1))
                }
                val semester = Semester(yearList, listOf("1", "2"), 0, termIndex)
                semester.account = user.account
                emit(semester)
            }
        }
    }
}