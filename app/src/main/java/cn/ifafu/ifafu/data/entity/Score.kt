package cn.ifafu.ifafu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Score {
    @PrimaryKey
    var id: Long = 0L
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
    var isIESItem: Boolean = true //是否记入智育分，仅用于成绩筛选！！
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

    override fun toString(): String {
        return "Score{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nature='" + nature + '\'' +
                ", attr='" + attr + '\'' +
                ", credit=" + credit +
                ", score=" + score +
                ", makeupScore=" + makeupScore +
                ", restudy=" + restudy +
                ", institute='" + institute + '\'' +
                ", gpa=" + gpa +
                ", remarks='" + remarks + '\'' +
                ", makeupRemarks='" + makeupRemarks + '\'' +
                ", isIESItem=" + isIESItem +
                ", account='" + account + '\'' +
                ", year='" + year + '\'' +
                ", term='" + term + '\'' +
                '}'
    }

    companion object {
        const val FREE_COURSE = -99999F //免修课程
    }
}
