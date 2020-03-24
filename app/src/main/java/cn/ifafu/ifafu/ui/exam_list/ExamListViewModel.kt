package cn.ifafu.ifafu.ui.exam_list

import android.app.Application
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.bean.Semester
import cn.woolsen.easymvvm.livedata.LiveDataBoolean
import cn.woolsen.easymvvm.livedata.LiveDataField
import cn.woolsen.easymvvm.livedata.LiveDataString
import kotlinx.coroutines.*

class ExamListViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var _semester: Semester
    val semester = LiveDataField<Semester>()

    val exams = LiveDataField<List<Exam>>()
    val toastMessage = LiveDataString()
    val loading = LiveDataBoolean()

    private val now = System.currentTimeMillis()
    private val examTimeComparator = Comparator<Exam> { o1, o2 ->
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

    init {
        GlobalScope.launch {
            loading.postValue(true)
            _semester = RepositoryImpl.getNowSemester()
            _semester.yearList.remove("全部")
            _semester.termList.remove("全部")
            semester.postValue(_semester)
            RepositoryImpl.getExamsFromDbOrNet(_semester.yearStr, _semester.termStr).getOrFailure {
                exams.postValue(emptyList())
                toastMessage.postValue(it.errorMessage())
            }?.let {
                exams.postValue(it.sortedWith(examTimeComparator))
            }
            loading.postValue(false)
        }
    }

    fun refresh() = GlobalScope.launch {
        loading.postValue(true)
        kotlin.runCatching {
            val resp = RepositoryImpl.fetch(_semester.yearStr, _semester.termStr)
            if (resp.isSuccess && resp.data != null) {
                exams.postValue(resp.data.sortedWith(examTimeComparator))
            } else {
                toastMessage.postValue(resp.message)
            }
        }.onFailure {
            toastMessage.postValue(it.errorMessage())
        }
        loading.postValue(false)
    }

    fun switchYearAndTerm(yearIndex: Int, termIndex: Int) = GlobalScope.launch {
        event.showDialog()
        try {
            _semester.yearIndex = yearIndex
            _semester.termIndex = termIndex
            semester.postValue(_semester)
            RepositoryImpl.getExamsFromDbOrNet(_semester.yearStr, _semester.termStr).getOrFailure {
                exams.postValue(emptyList())
                toastMessage.postValue(it.errorMessage())
            }?.let {
                exams.postValue(it.sortedWith(examTimeComparator))
            }
        } catch (e: Exception) {
            toastMessage.postValue(e.errorMessage())
        }
        event.hideDialog()
    }

    private fun fetchAsync(semester: Semester) = GlobalScope.async {
        RepositoryImpl.fetch(semester.yearStr, semester.termStr).data
    }


}
