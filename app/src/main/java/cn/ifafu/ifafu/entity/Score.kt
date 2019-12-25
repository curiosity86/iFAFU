package cn.ifafu.ifafu.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Score {
    @PrimaryKey
    var id: Long = 0L
    var name //课程名称
            : String = ""
    var nature //课程性质
            : String = ""
    var attr //课程归属
            : String = ""
    var credit //学分
            : Float = 0F
    var score //成绩
            : Float = 0F
        private set
    var makeupScore //补考成绩
            : Float = 0F
    var restudy = false //是否重修
    var institute //开课学院
            : String = ""
    var gpa //绩点
            : Float = -1F
    var remarks //备注
            : String = ""
    var makeupRemarks //补考备注
            : String = ""
    var isIESItem //是否记入智育分
            : Boolean = false
    var account: String? = null
    var year: String = ""
    var term: String= ""

    constructor() {
    }

    fun setScore(score: Float) { //学分免修课程默认不计入智育分
        if (score == FREE_COURSE) {
            isIESItem = false
        }
        this.score = score
    }//及格//补考成绩不及格，以补考成绩计算，并以学分1:1比例扣除相应智育分//补考成绩及格，以60分计算//补考成绩未出，以原成绩计算

    //不及格
    val realScore: Float
        get() {
            if (score == -1F) {
                return 0f
            } else if (score == FREE_COURSE) {
                return FREE_COURSE
            }
            return if (score < 60) { //不及格
                if (makeupScore == -1F) { //补考成绩未出，以原成绩计算
                    score
                } else if (makeupScore >= 60) { //补考成绩及格，以60分计算
                    60f
                } else { //补考成绩不及格，以补考成绩计算，并以学分1:1比例扣除相应智育分
                    makeupScore
                }
            } else { //及格
                score
            }
        }

    val calcScore: Float
        get() {
            if (score == -1F) {
                return 0f
            } else if (score == FREE_COURSE) {
                return FREE_COURSE
            }
            return realScore * credit
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
        const val FREE_COURSE = -99999f //免修课程
    }
}