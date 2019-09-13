//package cn.ifafu.ifafu.data.entity
//
//import cn.ifafu.ifafu.view.syllabus.Weekday
//
///**
// * 调课详情，从 第x1周周y1 调到 第x2周周y2
// */
//class HolidayMap {
//
//    // Int: week shl 3 + weekday
//    // value为null,则为放假不上课
//    private val map: MutableMap<Int, Int?> = HashMap()
//
//    fun addFangJia(week: Int, weekday: Int) {
//        map[week shl weekday] = null
//    }
//
//    fun addTiaoKe(fromWeek: Int, @Weekday fromWeekday: Int,
//                  toWeek: Int, @Weekday toWeekday: Int) {
//        map[int(fromWeek, fromWeekday)] = int(toWeek, toWeekday)
//    }
//
//    /**
//     * @return  null 不调课也不放假
//     *          WAW isNull 放假
//     *              or 调课
//     */
//    fun find(week: Int, weekday: Int): WAW? {
//        val from = map[int(week, weekday)] ?: return null
//        from shr
//    }
//
//    fun int(week: Int, weekday: Int): Int {
//        return week shl 3 or weekday
//    }
//
//    class WAW(val week: Int = -1, val weekday: Int = -1) {
//
//        //Null
//        fun isNull(): Boolean {
//            return week == -1 || weekday == -1
//        }
//    }
//}