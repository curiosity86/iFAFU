package cn.ifafu.ifafu.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity
class Exam {
    @PrimaryKey
    var id: Long = 0L
    var name: String = ""
    var startTime: Long = 0
    var endTime: Long = 0
    var address: String = ""
    var seatNumber: String = ""
    var account: String = ""
    var year: String = ""
    var term: String = ""

    constructor(id: Long, name: String, startTime: Long, endTime: Long, address: String,
                seatNumber: String, account: String, year: String, term: String) {
        this.id = id
        this.name = name
        this.startTime = startTime
        this.endTime = endTime
        this.address = address
        this.seatNumber = seatNumber
        this.account = account
        this.year = year
        this.term = term
    }

    constructor() {}

    override fun toString(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.CHINA)
        return "Exam{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTime=" + format.format(Date(startTime)) +
                ", endTime=" + format.format(Date(endTime)) +
                ", address='" + address + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                ", account='" + account + '\'' +
                ", year='" + year + '\'' +
                ", term='" + term + '\'' +
                '}'
    }
}