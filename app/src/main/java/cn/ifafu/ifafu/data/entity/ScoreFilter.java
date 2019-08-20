package cn.ifafu.ifafu.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class ScoreFilter {
    @Id
    private Long id;
    private String name;
    @NotNull
    private boolean isFilter;

    @Generated(hash = 1120454222)
    public ScoreFilter(Long id, String name, boolean isFilter) {
        this.id = id;
        this.name = name;
        this.isFilter = isFilter;
    }

    @Generated(hash = 533651343)
    public ScoreFilter() {
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

    public boolean getIsFilter() {
        return this.isFilter;
    }

    public void setIsFilter(boolean isFilter) {
        this.isFilter = isFilter;
    }
}
