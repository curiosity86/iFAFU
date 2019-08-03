package cn.ifafu.ifafu.mvp.main;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.app.IFAFU;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import cn.ifafu.ifafu.data.Menu;
import cn.ifafu.ifafu.mvp.exam.ExamActivity;
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity;
import cn.woolsen.android.uitl.RxJavaUtils;
import io.reactivex.Observable;

class MainModel extends BaseZFModel implements MainContract.Model {

    private User user;

    MainModel(Context context) {
        super(context);
        user = IFAFU.getUser();
    }

    @Override
    public Observable<List<Menu>> getMenus() {
        return RxJavaUtils.create(emitter -> {
            List<Menu> menus = new ArrayList<>();
            menus.add(new Menu(R.drawable.tab_syllabus, "课程表", SyllabusActivity.class));
            menus.add(new Menu(R.drawable.tab_exam, "考试查询", ExamActivity.class));
            emitter.onNext(menus);
            emitter.onComplete();
        });
    }

    @Override
    public Drawable getSchoolIcon() {
        switch (user.getSchoolCode()) {
            case Constant.FAFU:
                return mContext.getDrawable(R.drawable.drawable_fafu2);
            case Constant.FAFU_JS:
                return mContext.getDrawable(R.drawable.drawable_fafu_js2);
            default:
                return mContext.getDrawable(R.drawable.drawable_ifafu2);
        }
    }

    @Override
    public String getUserName() {
        return user.getName();
    }


}
