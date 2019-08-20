package cn.ifafu.ifafu.mvp.main;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.BuildConfig;
import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.mvp.login.LoginActivity;
import cn.ifafu.ifafu.mvp.syllabus.SyllabusModel;
import cn.ifafu.ifafu.util.DateUtils;
import cn.ifafu.ifafu.util.GlobalLib;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;

public class MainPresenter extends BaseZFPresenter<MainContract.View, MainContract.Model>
        implements MainContract.Presenter {

    MainPresenter(MainContract.View view) {
        super(view, new MainModel(view.getContext()));
    }

    @Override
    public void onStart() {
        mView.setLeftMenuHeadName(mModel.getUserName());
        mView.setLeftMenuHeadIcon(mModel.getSchoolIcon());
        updateView();
        Thread thread = new Thread();
        thread.start();
        thread.interrupt();
        // 获取主页菜单
        mCompDisposable.add(mModel.getMenus()
                .compose(RxUtils.ioToMain())
                .subscribe(menus -> mView.setMenuAdapterData(menus), this::onError)
        );

    }

    @Override
    public void updateApp() {
        mCompDisposable.add(Observable
                .fromCallable(() -> {
                    UpgradeInfo info = Beta.getUpgradeInfo();
                    return info != null && info.versionCode > GlobalLib.getLocalVersionCode(mView.getActivity());
                })
                .compose(RxUtils.ioToMain())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        Beta.checkUpgrade();
                    } else {
                        mView.showMessage(R.string.is_last_version);
                    }
                }, this::onError)
        );
    }

    @Override
    public void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "iFAFU下载链接：http://ifafu.cn");
        mView.openActivity(Intent.createChooser(intent, "分享"));
    }

    @Override
    public void updateView() {
        if (mView.getActivity().getIntent().getIntExtra("come_from", -1) != 0) {
            loginD = reLogin()
                    .compose(RxUtils.ioToMain())
                    .subscribe(response -> {
                    }, this::onError);
            mCompDisposable.add(loginD);
        }
        // 获取天气
        mCompDisposable.add(mModel.getWeather("101230101")
                .compose(RxUtils.ioToMain())
                .subscribe(weather -> mView.setWeatherText(weather), this::onError)
        );
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void updateCourseView() {
        mCompDisposable.add(Observable.fromCallable(() -> {
                    SyllabusModel model = new SyllabusModel(mView.getContext());
                    Map<String, String> map = new HashMap<>();
                    List<Course> courses = model.getAllCoursesFromDB();
                    if (courses.isEmpty()) {
                        map.put("title", "暂无课程信息");
                        return map;
                    }
                    //计算下一节是第几节课
                    int[] intTime = model.getCourseBeginTime();
                    Date date = new Date();
                    int now = (int) (date.getTime() / 1000 % 10000);
                    int nextNode = 1;
                    for (int i = 0; i < intTime.length; i++) {
                        if (now < intTime[i]) {
                            nextNode = i;
                            break;
                        }
                    }
                    int currentWeek = model.getCurrentWeek();
                    if (currentWeek == -1) {
                        map.put("title", "放假中");
                        return map;
                    }
                    int currentWeekday = DateUtils.getCurrentDayOfWeek();
                    Course next = null;
                    for (int i = 1; i < courses.size(); i++) {
                        if (next == null && courses.get(i).getWeekday() == currentWeekday
                                || next != null
                                && courses.get(i).getWeekSet().contains(currentWeek)
                                && courses.get(i).getWeekday() == currentWeekday
                                && courses.get(i).getBeginNode() > nextNode
                                && courses.get(i).getBeginNode() < next.getBeginNode()) {
                            next = courses.get(i);
                        }
                    }
                    if (next != null) {
                        map.put("title", "下一节课：");
                        map.put("name", next.getName());
                        map.put("address", next.getAddress());
                        if (nextNode - 1 < intTime.length) {
                            int length = model.getOneNodeLength();
                            int intStartTime = intTime[next.getBeginNode() - 1];
                            int intEndTime = intTime[next.getBeginNode() + next.getNodeCnt() - 2];
                            if (intEndTime % 100 + length >= 60) {
                                intEndTime = intEndTime + 100 - (intEndTime % 100) + ((intEndTime % 100 + length) % 60);
                            }
                            map.put("time", String.format("%d:%02d-%d:%02d",
                                    intStartTime / 100,
                                    intStartTime % 100,
                                    intEndTime / 100,
                                    intEndTime % 100));
                        }
                    }
                    return map;
                })
                        .compose(RxUtils.computationToMain())
                        .subscribe(map -> {
                            String title = map.containsKey("title") ? map.get("title") : "";
                            String name = map.containsKey("name") ? map.get("name") : "";
                            String address = map.containsKey("address") ? map.get("address") : "";
                            String time = map.containsKey("time") ? map.get("time") : "";
                            mView.setCourseText(title, name, address, time);
                        }, throwable -> {
                            onError(throwable);
                            mView.setCourseText("获取课程信息失败", "", "", "");
                        })
        );
    }

    @Override
    public void quitAccount() {
        if (BuildConfig.DEBUG) {
            mModel.clearAllDate();
        }
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        mView.openActivity(intent);
        mView.killSelf();
    }
}
