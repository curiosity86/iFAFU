package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Objects;

@Entity
public class Exam {
    @Id
    private Long id;
    private String name;
    private String datetime;
    private String address;
    private String seatNumber;
    private String account;
    private boolean local;
    @Generated(hash = 1929023768)
    public Exam(Long id, String name, String datetime, String address,
            String seatNumber, String account, boolean local) {
        this.id = id;
        this.name = name;
        this.datetime = datetime;
        this.address = address;
        this.seatNumber = seatNumber;
        this.account = account;
        this.local = local;
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
    public String getDatetime() {
        return this.datetime;
    }
    public void setDatetime(String datetime) {
        this.datetime = datetime;
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
    public boolean getLocal() {
        return this.local;
    }
    public void setLocal(boolean local) {
        this.local = local;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exam exam = (Exam) o;
        return local == exam.local &&
                name.equals(exam.name) &&
                Objects.equals(datetime, exam.datetime) &&
                Objects.equals(address, exam.address) &&
                Objects.equals(seatNumber, exam.seatNumber) &&
                account.equals(exam.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, datetime, address, seatNumber, account, local);
    }
}
