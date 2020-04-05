package cn.ifafu.ifafu.experiment.ui.elective

import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.util.sumByFloat

class ElectiveViewModel(application: Application) : BaseViewModel(application) {

    val total = MutableLiveData<Elective>()
    val zrkx = MutableLiveData<Elective>()
    val rwsk = MutableLiveData<Elective>()
    val ysty = MutableLiveData<Elective>()
    val wxsy = MutableLiveData<Elective>()
    val cxcy = MutableLiveData<Elective>()

    val loading = MutableLiveData<String>()

    init {
        safeLaunch(block = {
            loading.postValue("加载中")
            val allScores = ensureLoginStatus {
                RepositoryImpl.ScoreRt.fetchAll()
            }.run {
                if (data != null) {
                    data.filter {
                        !it.remarks.contains("英语分级")
                    }
                } else {
                    toast(this.message)
                    loading.postValue(null)
                    return@safeLaunch
                }
            }
            //获取选修学分要求
            val electives = RepositoryImpl.ElectivesRt.get() ?: ensureLoginStatus {
                RepositoryImpl.ElectivesRt.fetch().apply {
                    RepositoryImpl.ElectivesRt.save(this)
                }
            }
            var totalScores = allScores.filter { it.nature == "任意选修课" || it.nature == "公共选修课" }
                    .sortedBy { it.name }
            for (i in 1 until totalScores.size) {
                if (totalScores[i].name == totalScores[i - 1].name) {
                    if (totalScores[i].score > totalScores[i - 1].score) {
                        if (totalScores[i - 1].score >= 60) {
                            totalScores[i - 1].credit = 0F
                        }
                    } else {
                        if (totalScores[i].score >= 60) {
                            totalScores[i].credit = 0F
                        }
                    }
                }
            }
            totalScores = totalScores.sortedByDescending { it.score }
            val zrkxScores = totalScores.filter { it.attr == "自然科学类" }
            val rwskScores = totalScores.filter { it.attr == "人文社科类" }
            val ystyScores = totalScores.filter { it.attr == "艺术体育类" || it.attr == "艺术、体育类" }
            val wxsyScores = totalScores.filter { it.attr == "文学素养类" }
            val cxcyScores = totalScores.filter { it.attr == "创新创业教育类" }
            val zrkxCredit = zrkxScores.filter { it.realScore >= 60 }
                    .sumByFloat { it.credit }
            val rwskCredit = rwskScores.filter { it.realScore >= 60 }
                    .sumByFloat { it.credit }
            val ystyCredit = ystyScores.filter { it.realScore >= 60 }
                    .sumByFloat { it.credit }
            val wxsyCredit = wxsyScores.filter { it.realScore >= 60 }
                    .sumByFloat { it.credit }
            val cxcyCredit = cxcyScores.filter { it.realScore >= 60 }
                    .sumByFloat { it.credit }
            val totalCredit = zrkxCredit + rwskCredit + ystyCredit + wxsyCredit + cxcyCredit
            val zrkxDone = zrkxCredit >= electives.zrkx
            val rwskDone = rwskCredit >= electives.rwsk
            val ystyDone = ystyCredit >= electives.ysty
            val wxsyDone = wxsyCredit >= electives.wxsy
            val cxcyDone = cxcyCredit >= electives.cxcy
            val totalDone = zrkxDone && rwskDone && ystyDone && wxsyDone && cxcyDone && totalCredit >= electives.total
            total.postValue(Elective("全部选修课，已修${totalScores.size}门",
                    "需修满${electives.total}分，已修${totalCredit}分${totalDone.isDoneStr()}",
                    totalScores, totalDone))
            zrkx.postValue(Elective("自然科学类，已修${zrkxScores.size}门",
                    "需修满${electives.zrkx}分，已修${zrkxCredit}分${zrkxDone.isDoneStr()}",
                    zrkxScores, zrkxDone))
            rwsk.postValue(Elective("人文社科类，已修${rwskScores.size}门",
                    "需修满${electives.rwsk}分，已修${rwskCredit}分${rwskDone.isDoneStr()}",
                    rwskScores, rwskDone))
            ysty.postValue(Elective("艺术体育类，已修${ystyScores.size}门",
                    "需修满${electives.ysty}分，已修${ystyCredit}分${ystyDone.isDoneStr()}",
                    ystyScores, ystyDone))
            wxsy.postValue(Elective("文学素养类，已修${wxsyScores.size}门",
                    "需修满${electives.wxsy}分，已修${wxsyCredit}分${wxsyDone.isDoneStr()}",
                    wxsyScores, wxsyDone))
            cxcy.postValue(Elective("创新创业教育类，已修${cxcyScores.size}门",
                    "需修满${electives.cxcy}分，已修${cxcyCredit}分${cxcyDone.isDoneStr()}",
                    cxcyScores, cxcyDone))
            loading.postValue(null)
        }, error = {
            toast(it.errorMessage())
            loading.postValue(null)
        })
    }

    private fun Boolean.isDoneStr(): String {
        return if (this) "（已修满）" else ""
    }

}