package cn.ifafu.ifafu.data.entity;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class Exam extends YearTerm {
    @Id
    private Long id;
    private String name;
    private long startTime;
    private long endTime;
    private String address;
    private String seatNumber;
    private String account;

    private String year;
    private String term;

    @Generated(hash = 1064214993)
    public Exam(Long id, String name, long startTime, long endTime, String address,
                String seatNumber, String account, String year, String term) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.address = address;
        this.seatNumber = seatNumber;
        this.account = account;
        this.year = year;
        this.term = term;
    }

    @Generated(hash = 945526930)
    public Exam() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSeatNumber() {
        return this.seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTerm() {
        return this.term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.CHINA);
        return "Exam{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTime=" + format.format(new Date(startTime)) +
                ", endTime=" + format.format(new Date(endTime)) +
                ", address='" + address + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                ", account='" + account + '\'' +
                ", year='" + year + '\'' +
                ", term='" + term + '\'' +
                '}';
    }
}
