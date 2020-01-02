package cn.ifafu.ifafu.mvp.elective

import android.app.Application
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.entity.ElectiveInfo
import cn.ifafu.ifafu.util.sumByFloat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ElectiveViewModel(application: Application) : BaseViewModel(application) {

    fun init(success: suspend (total: ElectiveInfo,
                               zrkx: ElectiveInfo?,
                               rwsk: ElectiveInfo?,
                               ysty: ElectiveInfo?,
                               wxsy: ElectiveInfo?,
                               cxcy: ElectiveInfo?) -> Unit) {
        GlobalScope.launch {
            event.showDialog()
            val allScores = mRepository.getAllScores().ifEmpty {
                try {
                    ensureLoginStatus {
                        mRepository.fetchScoreList().apply {
                            if (this.isNotEmpty()) {
                                mRepository.deleteAllScore()
                                mRepository.saveScore(this)
                                val scoreFilter = mRepository.getScoreFilter()
                                scoreFilter.account = mRepository.account
                                scoreFilter.filter(this)
                                mRepository.saveScoreFilter(scoreFilter)
                            }
                        }
                    } ?: return@launch
                } catch (e: Exception) {
                    event.showMessage(e.errorMessage())
                    event.hideDialog()
                    e.printStackTrace()
                    return@launch
                }
            }
            val electives = mRepository.getElectives() ?: try {
                ensureLoginStatus {
                    mRepository.fetchElectives().apply {
                        mRepository.saveElectives(this)
                    }
                } ?: return@launch
            } catch (e: Exception) {
                e.printStackTrace()
                event.showMessage(e.errorMessage())
                event.hideDialog()
                return@launch
            }
            val totalScores = allScores.filter { it.nature == "任意选修课" || it.nature == "公共选修课" }
                    .sortedByDescending { it.score }
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
            success(
                    ElectiveInfo("全部选修课，已修${totalScores.size}门",
                            "需修满${electives.total}分，已修${totalCredit}分${totalDone.isDoneStr()}",
                            totalScores, true),
                    ElectiveInfo("自然科学类，已修${zrkxScores.size}门",
                            "需修满${electives.zrkx}分，已修${zrkxCredit}分${zrkxDone.isDoneStr()}",
                            zrkxScores, zrkxDone),
                    ElectiveInfo("人文社科类，已修${rwskScores.size}门",
                            "需修满${electives.rwsk}分，已修${rwskCredit}分${rwskDone.isDoneStr()}",
                            rwskScores, rwskDone),
                    ElectiveInfo("艺术体育类，已修${ystyScores.size}门",
                            "需修满${electives.ysty}分，已修${ystyCredit}分${ystyDone.isDoneStr()}",
                            ystyScores, ystyDone),
                    ElectiveInfo("文学素养类，已修${wxsyScores.size}门",
                            "需修满${electives.wxsy}分，已修${wxsyCredit}分${wxsyDone.isDoneStr()}",
                            wxsyScores, wxsyDone),
                    ElectiveInfo("创新创业教育类，已修${cxcyScores.size}门",
                            "需修满${electives.cxcy}分，已修${cxcyCredit}分${cxcyDone.isDoneStr()}",
                            cxcyScores, cxcyDone))
            event.hideDialog()
        }
    }

    private fun Boolean.isDoneStr(): String {
        return if (this) "（已修满）" else ""
    }

}