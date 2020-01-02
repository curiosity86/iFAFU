package cn.ifafu.ifafu.entity

/**
 * 学年和学期选项
 * Created by woolsen on 19/9/18
 */
data class YearTerm(
        var yearList: List<String>,
        var termList: List<String>,
        var yearIndex: Int = 0,
        var termIndex: Int = 0
) {

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

    fun setYearTermIndex(yearIndex: Int, termIndex: Int) {
        this.yearIndex = yearIndex
        this.termIndex = termIndex
    }

}