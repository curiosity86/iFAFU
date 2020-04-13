package cn.ifafu.ifafu.experiment.data.repository

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
import cn.ifafu.ifafu.experiment.util.successMap
import cn.ifafu.ifafu.experiment.util.toMediatorLiveData
import cn.ifafu.ifafu.util.GlobalLib.calcIES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class ScoreRepository(
        private val userManager: UserManager,
        private val scoreDao: ScoreDao,
        private val zfService: ZFService
) {

    private val coroutineScope = GlobalScope

    val semester: LiveData<Semester>
        get() = _semester
    private val _semester = loadSemester().toMediatorLiveData()

    private var lock = false
    private var loadEmptyThenFetch = true
    private var dbResource: LiveData<List<Score>>? = null
    val scoreResource: LiveData<Resource<List<Score>>>
        get() = _scoreResource
    private val _scoreResource = MediatorLiveData<Resource<List<Score>>>().apply {
        removeSource(semester)
        //监听Semester数据
        addSource(semester) { semester ->
            dbResource?.let { removeSource(it) }
            //监听数据库数据
            loadEmptyThenFetch = true
            val dbResource = scoreDao.getAllScores(semester.account, semester.yearStr, semester.termStr)
            this@ScoreRepository.dbResource = dbResource
            addSource(dbResource) { scores ->
                if (!lock) {
                    if (loadEmptyThenFetch && scores.isEmpty()) {
                        refresh()
                        loadEmptyThenFetch = false
                    } else {
                        this.value = Resource.Success(scores)
                    }
                }
            }
        }
    }

    val ies: LiveData<Float> = _scoreResource.successMap { it.calcIES() }

    fun refresh() {
        val semester = semester.value ?: return
        val user = userManager.user.value ?: return
        coroutineScope.launch(Dispatchers.IO) {
            lock = true
            _scoreResource.postValue(Resource.Loading())
            //获取网络数据
            fetchScoresFromNetwork(user, semester.yearStr, semester.termStr).run {
                _scoreResource.postValue(this)
            }
            lock = false
        }
    }

    fun switchSemester(year: String, term: String) {
        val semester = _semester.value ?: return
        semester.yearIndex = semester.yearIndexOf(year)
        semester.termIndex = semester.termIndexOf(term)
        _semester.value = semester
    }

    private fun fetchScoresFromNetwork(user: User, year: String, term: String): Resource<List<Score>> {
        val response = userManager.auto {
            zfService.fetchScores(user, year, term)
        }
        return when (response) {
            is IFResponse.Success -> {
                val list = response.data.onEach {
                    it.account = user.account
                    it.id = it.hashCode()
                }
                //保存数据
                if (list.isNotEmpty()) {
                    scoreDao.deleteScore(user.account, year, term)
                    scoreDao.saveScore(list)
                }
                Resource.Success(list)
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
                val yearList = arrayListOf("全部")
                for (i in enrollmentYear until toYear) {
                    yearList.add(0, String.format(Locale.CHINA, "%d-%d", i, i + 1))
                }
                val semester = Semester(yearList, listOf("1", "2", "全部"), 0, termIndex)
                semester.account = user.account
                emit(semester)
            }
        }
    }

    fun saveScore(vararg score: Score) {
        scoreDao.saveScore(*score)
    }

    fun saveScore(score: List<Score>) {
        scoreDao.saveScore(score)
    }

}