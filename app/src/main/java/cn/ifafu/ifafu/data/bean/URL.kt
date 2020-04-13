package cn.ifafu.ifafu.data.bean


class URL(val host: String,
          val login: String,
          val verify: String,
          val main: String,
          val exam: Pair<String, String>,
          val score: Pair<String, String>
) {
    companion object {
        const val DEFAULT = "default"
        const val LOGIN = "login"
        const val VERIFY = "verify"
        const val MAIN = "main"
        const val EXAM = "exam"
        const val SCORE = "score"
    }
}