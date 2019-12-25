package cn.ifafu.ifafu.entity

class Weather {
    var cityName: String = ""
    var nowTemp = 0
    var amTemp = 0
    var pmTemp = 0
    var weather: String = ""

    constructor() {}
    constructor(cityName: String) {
        this.cityName = cityName
    }

    override fun toString(): String {
        return "Weather{" +
                "cityName='" + cityName + '\'' +
                ", nowTemp=" + nowTemp +
                ", amTemp=" + amTemp +
                ", pmTemp=" + pmTemp +
                ", weather='" + weather + '\'' +
                '}'
    }
}