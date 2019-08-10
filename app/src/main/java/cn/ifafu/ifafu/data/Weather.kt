package cn.ifafu.ifafu.data

data class Weather(
        var cityName: String? = null,
        var nowTemp: Int = 0,
        var amTemp: Int = 0,
        var pmTemp: Int = 0,
        var weather: String? = null
)