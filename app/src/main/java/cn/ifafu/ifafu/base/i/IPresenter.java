package cn.ifafu.ifafu.base.i;

public interface IPresenter {

    default void onCreate() {};

    void onDestroy();
}
