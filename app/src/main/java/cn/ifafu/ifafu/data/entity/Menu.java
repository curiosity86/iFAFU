package cn.ifafu.ifafu.data.entity;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class Menu {

    private Drawable icon; //图标
    private String title; //标题
    private Intent intent;

    public Menu(Drawable icon, String title, Intent intent) {
        this.icon = icon;
        this.title = title;
        this.intent = intent;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public Intent getIntent() {
        return intent;
    }
}
