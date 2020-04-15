package cn.ifafu.ifafu.experiment.data.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import cn.ifafu.ifafu.data.bean.Semester
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.experiment.bean.IFResponse
import cn.ifafu.ifafu.experiment.bean.Resource
import cn.ifafu.ifafu.experiment.data.UserManager
import cn.ifafu.ifafu.experiment.data.db.ScoreDao
import cn.ifafu.ifafu.experiment.data.service.ZFService
import cn.ifafu.ifafu.experiment.util.toMediatorLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class ScoreRepository(
        private val userManager: UserManager,
        private val scoreDao: ScoreDao,
        private val zfService: ZFService
) {

    private val coroutineScope = GlobalScope

    val semester: LiveData<Semester>
        get() = _semester
    private val _semester = loadSemester().toMediatorLiveData()

    private var lockOnce = false

    val scoreResource: LiveData<Resource<List<Score>>>
        get() = _scoreResource
    private val _scoreResource = MediatorLiveData<Resource<List<Score>>>().apply {
        addSource(userManager.user) { user ->
            //监听Semester数据
            removeSource(_semester)
            addSource(_semester) { s ->
                coroutineScope.launch(Dispatchers.IO) {
                    val dbResource = loadScoresFromDbL(user.account, s.yearStr, s.termStr)
                    if (dbResource.isNotEmpty()) {
                        postValue(Resource.Success(dbResource))
                    } else {
                        postValue(Resource.Loading())
                        //获取网络数据
                        fetchScoresFromNetworkAndSave(user, s.yearStr, s.termStr).run {
                            postValue(this)
                        }
                    }
                }
            }

        }
    }

    fun refresh() {
        val semester = _semester.value ?: return
        val user = userManager.user.value ?: return
        coroutineScope.launch(Dispatchers.IO) {
            _scoreResource.postValue(Resource.Loading())
            //获取网络数据
            fetchScoresFromNetworkAndSave(user, semester.yearStr, semester.termStr).run {
                Timber.d("ScoreRepository#refresh#postValue")
                _scoreResource.postValue(this)
            }
        }
    }

    fun switchSemester(year: String, term: String) {
        val semester = _semester.value ?: return
        semester.yearIndex = semester.yearIndexOf(year)
        semester.termIndex = semester.termIndexOf(term)
        _semester.value = semester
    }

    private fun fetchScoresFromNetworkAndSave(user: User, year: String, term: String): Resource<List<Score>> {
        val response = userManager.auto {
            if (user.school == User.FAFU || user.school == User.FAFU_JS) {
                zfService.fetchScores(user, "全部", "全部")
            } else {
                zfService.fetchScores(user, year, term)
            }
        }
        return when (response) {
            is IFResponse.Success -> {
                val list = response.data
                        .onEach {
                            it.account = user.account
                            it.id = it.hashCode()
                        }
                        .sortedBy { it.id }
                if (list.isNotEmpty()) {
                    lockOnce = true
                    scoreDao.deleteScore(user.account, year, term)
                    scoreDao.saveScore(list)
                }
                Resource.Success(list.filter(year, term)).apply {
                    message = "获取成功"
                }
            }
            is IFResponse.Failure -> {
                Resource.Error(response.message)
            }
            is IFResponse.Error -> {
                response.exception.printStackTrace()
                Resource.Error("获取成绩出错")
            }
            is IFResponse.NoAuth -> {
                Resource.Error("获取成绩出错(Error:NoAuth)")
            }
        }
    }

    private fun loadScoresFromDbL(account: String, year: String, term: String): List<Score> {
        return if (year == "全部" && term == "全部") {
            scoreDao.getAllScoresL(account)
        } else if (year == "全部") {
            scoreDao.getAllScoresByTermL(account, term)
        } else if (term == "全部") {
            scoreDao.getAllScoresByYearL(account, year)
        } else {
            scoreDao.getAllScoresL(account, year, term)
        }
    }

    private fun loadScoresFromDb(account: String, year: String, term: String): LiveData<List<Score>> {
        return if (year == "全部" && term == "全部") {
            scoreDao.getAllScores(account)
        } else if (year == "全部") {
            scoreDao.getAllScoresByTerm(account, term)
        } else if (term == "全部") {
            scoreDao.getAllScoresByYear(account, year)
        } else {
            scoreDao.getAllScores(account, year, term)
        }
    }

    private fun List<Score>.filter(year: String, term: String): List<Score> {
        return if (year == "全部" && term == "全部") {
            this
        } else if (year == "全部") {
            this.filter { it.term == term }
        } else if (term == "全部") {
            this.filter { it.year == year }
        } else {
            this.filter { it.year == year && it.term == term }
        }
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
                val termList = arrayListOf("1", "2")
                if (user.school == User.FAFU || user.school == User.FAFU_JS) {
                    termList.add("全部")
                    yearList.add("全部")
                }
                val semester = Semester(yearList, termList, 0, termIndex)
                semester.account = user.account
                emit(semester)
            }
        }
    }

    @WorkerThread
    fun saveScore(vararg score: Score) {
        scoreDao.saveScore(*score)
        //手动更新LiveData
        val resource = _scoreResource.value
        if (resource is Resource.Success) {
            val list = resource.data.toMutableList()
            list += score
            _scoreResource.postValue(Resource.Success(list))
        }
    }

    @WorkerThread
    fun saveScore(score: List<Score>) {
        scoreDao.saveScore(score)
        //手动更新LiveData
        val resource = _scoreResource.value
        if (resource is Resource.Success) {
            val list = resource.data.toMutableList()
            list += score
            _scoreResource.postValue(Resource.Success(list))
        }
    }

}