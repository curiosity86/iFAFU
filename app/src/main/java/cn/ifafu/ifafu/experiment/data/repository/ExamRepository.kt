package cn.ifafu.ifafu.experiment.data.repository

import androidx.lifecycle.LiveData
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.experiment.bean.IFResponse
import cn.ifafu.ifafu.experiment.bean.Resource
import cn.ifafu.ifafu.experiment.data.UserManager
import cn.ifafu.ifafu.experiment.data.db.ExamDao
import cn.ifafu.ifafu.experiment.data.service.ZFService
import cn.ifafu.ifafu.experiment.util.NetBoundResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

class ExamRepository(
        private val userManager: UserManager,
        private val examDao: ExamDao,
        private val zfService: ZFService
) {

    private val coroutineScope: CoroutineScope = GlobalScope

    fun loadExams(year: String, term: String): LiveData<Resource<List<Exam>>> {
        return userManager.userSwitchMap { user ->
            val account = user.account
            object : NetBoundResource<List<Exam>>(coroutineScope) {

                override fun loadFromDb(): LiveData<List<Exam>> {
                    return if (year == "全部" && term == "全部") {
                        examDao.getAllExams(account)
                    } else if (year == "全部") {
                        examDao.getAllExamsByTerm(account, term)
                    } else if (term == "全部") {
                        examDao.getAllExamsByYear(account, year)
                    } else {
                        examDao.getAllExams(account, year, term)
                    }
                }

                override fun shouldFetch(data: List<Exam>?): Boolean {
                    return true
                }

                override fun createCall(): IFResponse<List<Exam>> {
                    return userManager.auto { zfService.fetchExams(user, year, term) }
                }

                override fun saveCallResult(item: List<Exam>) {
                    val list = item.onEach {
                        it.account = account
                        it.id = hashCode()
                    }
                    examDao.saveScores(list)
                }

            }.asLiveData()
        }
    }

}