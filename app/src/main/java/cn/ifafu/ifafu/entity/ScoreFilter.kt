package cn.ifafu.ifafu.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class ScoreFilter {
    @PrimaryKey
    var account: String = ""
    var filterList: HashSet<Long> = HashSet() //过滤成绩id列表

    private fun filter(score: Score) {
        if (score.score == Score.FREE_COURSE
                || score.nature.contains("任意选修")
                || score.nature.contains("公共选修")
                || score.name.contains("体育")) {
            filterList.add(score.id)
        }
    }

    fun filter(scores: List<Score>) {
        scores.forEach { score ->
            filter(score)
        }
    }

    fun calcIES(scores: List<Score>): Float {
        if (scores.isEmpty()) {
            return 0f
        }
        var totalScore = 0f
        var totalCredit = 0f
        var totalMinus = 0f
        for (score in scores) {
            if (score.id !in filterList && score.realScore != Score.FREE_COURSE) {
                val realScore = score.realScore
                totalScore += (realScore * score.credit)
                totalCredit += score.credit
                if (realScore < 60) {
                    totalMinus -= score.credit
                }
            }
        }
        var result = totalScore / totalCredit - totalMinus
        if (result.isNaN()) {
            result = 0F
        }
        return result
    }

}
