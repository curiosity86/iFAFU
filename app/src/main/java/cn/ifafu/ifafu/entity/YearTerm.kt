package cn.ifafu.ifafu.entity

/**
 * 学年和学期选项
 * Created by woolsen on 19/9/18
 */
class YearTerm(
        var yearList: MutableList<String>,
        var termList: MutableList<String>,
        var yearIndex: Int = 0,
        var termIndex: Int = 0
) {

    val yearStr: String
        get() = yearList[yearIndex]

    val termStr: String
        get() = termList[termIndex]

    fun addYear(year: String) {
        yearList.add(year)
    }

    fun addYear(index: Int, year: String) {
        yearList.add(index, year)
    }

    fun addTerm(term: String) {
        termList.add(term)
    }

    fun addTerm(index: Int, term: String) {
        termList.add(index, term)
    }

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