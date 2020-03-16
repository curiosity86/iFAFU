package cn.ifafu.ifafu.ui.main.oldTheme.bean

class ExamPreview(
        val hasInfo: Boolean,
        val message: String = "",
        val items: Array<Item?> = arrayOfNulls(2)
) {
    class Item(
        val examName: String,
        val examTime: String,
        val address: String,
        val seatNumber: String,
        val timeLeftAndUnit: Array<String>
    )
}
