package cn.ifafu.ifafu.data.new_http

class SemesterOption(
        val years: List<String>,
        val terms: List<String>,
        val defaultYear: String, //默认学年
        val defaultTerm: String //默认学期
)