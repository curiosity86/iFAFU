package cn.ifafu.ifafu.ui.exam_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.bean.Semester
import cn.woolsen.easymvvm.livedata.LiveDataField
import kotlinx.coroutines.*
import timber.log.Timber

class ExamListViewModel(application: Application) : BaseViewModel(application) {

    val examList = LiveDataField<List<Exam>>()
    val semester = LiveDataField<Semester>()

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

    fun initData() {
        safeLaunch(block = {
            event.showDialog()
            val semester = Repository.getNowSemester()
            semester.yearList.remove("全部")
            semester.termList.remove("全部")
            withContext(Dispatchers.Main) {
                this@ExamListViewModel.semester.value = semester
            }
            this@ExamListViewModel.semester.postValue(semester)
            val exams = Repository.exam.getAll(semester.yearStr, semester.termStr)
                    .sortedWith(examTimeComparator)
            if (exams.isNotEmpty()) {
                examList.postValue(exams)
                event.hideDialog()
            }
            innerFetch().join()
            if (exams.isEmpty()) {
                event.hideDialog()
            }
        }, error = {
            event.hideDialog()
            event.showMessage(it.errorMessage())
        })
    }

    fun update() {
        safeLaunch(block = {
            event.showDialog()
            innerFetch().join()
            event.hideDialog()
        }, error = {
            event.showMessage(it.errorMessage())
            event.hideDialog()
        })
    }

    private fun innerFetch(): Job {
        return safeLaunchWithMessage {
            val semester = semester.value!!
            val response = Repository.exam.fetch(semester.yearStr, semester.termStr)
            if (!response.isSuccess) {
                event.showMessage(response.message)
                return@safeLaunchWithMessage
            }
            if (!response.data.isNullOrEmpty()) {
                examList.postValue(response.data.sortedWith(examTimeComparator))
            }
        }
    }

    fun switchYearAndTerm(yearIndex: Int, termIndex: Int) = GlobalScope.launch {
        try {
            event.showDialog()
            val semester = semester.value!!
            semester.setYearTermIndex(yearIndex, termIndex)
            this@ExamListViewModel.semester.postValue(semester)
            val exams = Repository.exam.getAll(semester.yearStr, semester.termStr)
            if (exams.isEmpty()) {
                innerFetch().join()
            }
            this@ExamListViewModel.examList.postValue(
                    exams.sortedWith(examTimeComparator))
        } catch (e: Exception) {
            event.showMessage(e.errorMessage())
        }
        event.hideDialog()
    }

}
