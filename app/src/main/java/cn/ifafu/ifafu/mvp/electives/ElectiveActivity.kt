package cn.ifafu.ifafu.mvp.electives

import android.os.Bundle
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity

class ElectiveActivity : BaseActivity<ElectiveContract.Presenter>(), ElectiveContract.View {
    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.activity_elective
    }

    override fun initData(savedInstanceState: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
