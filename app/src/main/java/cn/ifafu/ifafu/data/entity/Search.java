package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Search {

    @Id
    private Long id;

    private String year;

    private String term;

    @Generated(hash = 903714601)
    public Search(Long id, String year, String term) {
        this.id = id;
        this.year = year;
        this.term = term;
    }

    @Generated(hash = 1644193961)
    public Search() {
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
