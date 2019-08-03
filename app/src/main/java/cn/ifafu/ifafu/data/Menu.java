package cn.ifafu.ifafu.data;

import android.graphics.drawable.Drawable;

public class Menu {

    private Drawable icon; //图标
    private int iconResId = 0;
    private String title; //标题
    private Class activityClass; //启动Class

    public Menu() {
    }

    public Menu(int iconResId, String title, Class activityClass) {
        this.iconResId = iconResId;
        this.title = title;
        this.activityClass = activityClass;
    }

    public Menu(Drawable icon, String title, Class activityClass) {
        this.icon = icon;
        this.title = title;
        this.activityClass = activityClass;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class activityClass) {
        this.activityClass = activityClass;
    }
}
