package cn.ifafu.ifafu.data.bean

class Vocation(
        var name: String,//节假日名称
        var date: String,//开始放假时间
        var day: Int,//放假天数
        var fromTo: Map<String, String> = emptyMap()//调课方式， 把key的课调到value
)