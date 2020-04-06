package cn.ifafu.ifafu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alibaba.fastjson.JSONObject
import timber.log.Timber

@Entity
class Score {
    @PrimaryKey
    var id: Int = 0
    var name: String = ""//课程名称
    var nature: String = "" //课程性质
    var attr: String = "" //课程归属
    var credit: Float = -1F //学分
    var score: Float = -1F //成绩
    var makeupScore: Float = -1F //补考成绩
    var restudy = false //是否重修
    var institute: String = "" //开课学院
    var gpa: Float = -1F //绩点
    var remarks: String = "" //备注
    var makeupRemarks: String = "" //补考备注
    var isIESItem: Boolean = true //是否记入智育分
    var account: String = ""
    var year: String = ""
    var term: String = ""

    //实际成绩，用于计算智育分的成绩
    val realScore: Float
        get() = when {
            score == -1F -> 0f
            score == FREE_COURSE -> FREE_COURSE //免修
            score < 60 -> { //不及格
                when {
                    makeupScore == -1F -> score //补考成绩未出，以原成绩计算
                    makeupScore >= 60F -> 60f //补考成绩及格，以60分计算
                    else -> makeupScore //补考成绩不及格，以补考成绩计算，并以学分1:1比例扣除相应智育分
                }
            }
            else -> score //及格，按正常成绩计算
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Score
        if (name != other.name) return false
        if (nature != other.nature) return false
        if (attr != other.attr) return false
        if (credit != other.credit) return false
        if (restudy != other.restudy) return false
        if (institute != other.institute) return false
        if (account != other.account) return false
        if (year != other.year) return false
        if (term != other.term) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + nature.hashCode()
        result = 31 * result + attr.hashCode()
        result = 31 * result + credit.hashCode()
        result = 31 * result + restudy.hashCode()
        result = 31 * result + institute.hashCode()
        result = 31 * result + account.hashCode()
        result = 31 * result + year.hashCode()
        result = 31 * result + term.hashCode()
        return result
    }

    companion object {
        const val FREE_COURSE = -99999F //免修课程
    }
}

fun List<Score>.calcIES(): Float {
    // 过滤 不计入智育分的成绩 和 免修成绩
    val scores = this
            .filter { it.isIESItem && it.realScore != Score.FREE_COURSE }
    if (scores.isEmpty()) {
        return 0f
    }
    var totalScore = 0f
    var totalCredit = 0f
    var totalMinus = 0f
    for (score in scores) {
        val realScore = score.realScore
        totalScore += (realScore * score.credit)
        totalCredit += score.credit
        if (realScore < 60) {
            totalMinus += score.credit
        }
    }
    var result = totalScore / totalCredit - totalMinus
    if (result.isNaN()) {
        result = 0F
    }
    Timber.d("IES, result = [${result}]")
    Timber.d("IES, firsh = [${JSONObject.toJSONString(scores[0])}]")
    Timber.d("IES, list = [${JSONObject.toJSONString(scores.map { it.name })}]")

    return result
}