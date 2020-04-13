package cn.ifafu.ifafu.experiment.vo

class SemesterStr(val year: String, val term: String) {
    override fun toString(): String {
        return if (year == "全部" && term == "全部") {
            "全部"
        } else if (term == "全部") {
            "${year}学年全部学期"
        } else {
            "${year}学年第${term}学期"
        }
    }
}