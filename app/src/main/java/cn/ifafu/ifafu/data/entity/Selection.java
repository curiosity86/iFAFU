package cn.ifafu.ifafu.data.entity;

import androidx.annotation.NonNull;

import java.util.List;

public class Selection {
    private String id;
    private String name;
    private int next;
    private List<Selection> data;

    public Selection() {
    }

    public Selection(String id, String name, int next, List<Selection> data) {
        this.id = id;
        this.name = name;
        this.next = next;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public List<Selection> getData() {
        return data;
    }

    public void setData(List<Selection> data) {
        this.data = data;
    }

    @Override
    @NonNull
    public String toString() {
        return "Selection{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", next=" + next +
                ", data=" + data +
                '}';
    }
}