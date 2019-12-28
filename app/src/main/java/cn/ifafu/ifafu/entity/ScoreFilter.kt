package cn.ifafu.ifafu.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class ScoreFilter {
    @PrimaryKey
    var account: String = ""
    var filterList: MutableList<Long> = ArrayList() //过滤成绩id列表

    fun filter(score: Score) {
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

}
