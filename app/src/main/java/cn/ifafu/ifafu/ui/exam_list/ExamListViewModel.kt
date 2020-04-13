package cn.ifafu.ifafu.ui.exam_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.bean.Semester
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExamListViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var _semester: Semester
    val semester = MutableLiveData<Semester>()

    val exams = MutableLiveData<List<Exam>>()
    val loading = MutableLiveData<String>()


    private val sort: List<Exam>.() -> List<Exam> = {
        val now = System.currentTimeMillis()
        val comparator = Comparator<Exam> { o1, o2 ->
            val i1 = o1.startTime - now
            val i2 = o2.startTime - now
            if (i1 > 0 && i2 > 0) {
                i1.compareTo(i2)
            } else if (i1 > 0) {
                -1
            } else if (i2 > 0) {
                1
            } else {
                i2.compareTo(i1)
            }
        }
        this.sortedWith(comparator)
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loading.postValue("加载中")
            _semester = RepositoryImpl.getExamSemester()
            semester.postValue(_semester)
            RepositoryImpl.getNotExamsFromDbOrNet().getOrFailure {
                exams.postValue(emptyList())
                toast(it.errorMessage())
            }?.let {
                exams.postValue(it.sort())
            }
            loading.postValue(null)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            loading.postValue("获取中")
            kotlin.runCatching {
                val resp = RepositoryImpl.fetch(_semester.yearStr, _semester.termStr)
                if (resp.isSuccess && resp.data != null) {
                    exams.postValue(resp.data.sort())
                } else {
                    toast(resp.message)
                }
            }.onFailure {
                toast(it.errorMessage())
            }
            loading.postValue(null)
        }
    }

    fun switchYearAndTerm(year: String, term: String) {
        viewModelScope.launch {
            loading.postValue("加载中")
            try {
                _semester.yearIndex = _semester.yearList.indexOf(year)
                _semester.termIndex = _semester.termList.indexOf(term)
                semester.postValue(_semester)
                RepositoryImpl.getExamsFromDbOrNet(year, term).getOrFailure {
                    exams.postValue(emptyList())
                    toast(it.errorMessage())
                }?.let {
                    exams.postValue(it.sort())
                }
            } catch (e: Exception) {
                toast(e.errorMessage())
            }
            loading.postValue(null)
        }
    }
}
