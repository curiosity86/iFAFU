package cn.ifafu.ifafu.ui.exam_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.Semester
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class ExamListViewModel(application: Application) : BaseViewModel(application) {

    val examList by lazy { MutableLiveData<List<Exam>>() }
    val semester by lazy { MutableLiveData<Semester>() }

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
        return safeLaunch {
            val semester = semester.value!!
            val response = Repository.exam.fetch(semester.yearStr, semester.termStr)
            if (!response.isSuccess) {
                event.showMessage(response.message)
                return@safeLaunch
            }
            examList.postValue(response.data)
        }
    }

    fun switchYearAndTerm(yearIndex: Int, termIndex: Int) {
        safeLaunch(block = {
            event.showDialog()
            val semester = semester.value!!
            semester.setYearTermIndex(yearIndex, termIndex)
            this@ExamListViewModel.semester.postValue(semester)
            val exams = Repository.exam.getAll(semester.yearStr, semester.termStr)
            if (exams.isEmpty()) {
                innerFetch().join()
            }
            this@ExamListViewModel.examList.postValue(exams)
            event.hideDialog()
        }, error = {
            event.showMessage(it.errorMessage())
            event.hideDialog()
        } )
    }

}
