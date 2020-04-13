package cn.ifafu.ifafu.data.bean

/**
 * 学年和学期选项
 * Created by woolsen on 19/9/18
 */
data class Semester(
        var yearList: List<String>,
        var termList: List<String>,
        var yearIndex: Int = 0,
        var termIndex: Int = 0
) {

    var account = ""

    val yearStr: String
        get() = yearList[yearIndex]

    val termStr: String
        get() = termList[termIndex]

    fun yearIndexOf(year: String): Int {
        return yearList.indexOf(year)
    }

    fun termIndexOf(term: String): Int {
        return termList.indexOf(term)
    }

    fun toTitle(): String {
        return if (termStr == "全部" && yearStr == "全部") {
            "全部"
        } else if (termStr == "全部") {
            "${yearStr}学年全部"
        } else {
            "${yearStr}学年第${termStr}学期"
        }
    }
}