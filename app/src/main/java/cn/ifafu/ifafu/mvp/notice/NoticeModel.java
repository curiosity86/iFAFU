package cn.ifafu.ifafu.mvp.notice;

import android.content.Context;

import java.util.Collection;
import java.util.List;

import cn.ifafu.ifafu.dao.NoticeDao;
import cn.ifafu.ifafu.data.dao.DaoManager;
import cn.ifafu.ifafu.data.entity.Notice;
import cn.woolsen.android.mvp.BaseModel;

class NoticeModel extends BaseModel implements NoticeContract.Model {

    private NoticeDao noticeDao = DaoManager.getInstance().getDaoSession().getNoticeDao();

    NoticeModel(Context context) {
        super(context);
    }

}
