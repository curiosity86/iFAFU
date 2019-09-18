package cn.ifafu.ifafu.mvp.main

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.data.entity.Menu
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.mvp.base.BaseActivity
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.mvp.other.AboutActivity
import cn.ifafu.ifafu.util.ButtonUtils
import cn.ifafu.ifafu.util.SPUtils
import cn.ifafu.ifafu.view.adapter.AccountAdapter
import cn.ifafu.ifafu.view.adapter.MenuAdapter
import cn.ifafu.ifafu.view.custom.DragLayout
import cn.ifafu.ifafu.view.timeline.TimeAxis
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bumptech.glide.Glide
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_account.view.*
import kotlinx.android.synthetic.main.include_left_menu.*
import kotlinx.android.synthetic.main.include_main.*
import kotlinx.android.synthetic.main.include_main_next_course.*
import kotlinx.android.synthetic.main.include_main_weather.*

class MainActivity : BaseActivity<MainContract.Presenter>(), MainContract.View, View.OnClickListener {

    private var mMenuAdapter: MenuAdapter? = null

    private var mAccountAdapter: AccountAdapter? = null

    private var checkoutDialog: MaterialDialog? = null

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        StatusBarUtil.setTransparent(this)
        val contentView = window.decorView.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        contentView.getChildAt(0).fitsSystemWindows = false
        mPresenter = MainPresenter(this)

        btn_menu.setOnClickListener(this)
        tv_nav_about.setOnClickListener(this)
        tv_nav_share.setOnClickListener(this)
        tv_nav_fback.setOnClickListener(this)
        tv_nav_update.setOnClickListener(this)
        tv_nav_checkout.setOnClickListener(this)

        if (!SPUtils.get(Constant.SP_USER_INFO).getBoolean("FIRST_START2")) {
            MaterialDialog(this).show {
                cancelable(false)
                title(text = "通知（Show only once）")
                message(text = "\t\t已支持节假日调课，调课的课程末尾会有[补课]提示，但是没有明显的UI区别，暂时没啥好的想法，所以就等下个版本再说啦。\n" +
                        "\t\t对于放假与调课UI有什么好的建议，可以直接滴滴我(27907670(雾深水浅))，或者在反馈群提建议，群号：663114635(关于那也有群号)")
                positiveButton(text = "收到")
            }
            SPUtils.get(Constant.SP_USER_INFO).putBoolean("FIRST_START2", true)
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter.updateNextCourseView()
    }

    override fun setMenuAdapterData(menus: List<Menu>) {
        if (mMenuAdapter == null) {
            mMenuAdapter = MenuAdapter(this, menus)
            mMenuAdapter!!.setOnMenuClickListener { _, menu ->
                if (!ButtonUtils.isFastDoubleClick(Constant.ACTIVITY_MAIN)) {
                    openActivity(menu.intent)
                }
            }
            rv_menu.layoutManager = GridLayoutManager(
                    this, 4, RecyclerView.VERTICAL, false)
            rv_menu.adapter = mMenuAdapter
        } else {
            mMenuAdapter!!.setMenuList(menus)
        }
    }

    override fun setLeftMenuHeadIcon(headIcon: Drawable) {
        Glide.with(this)
                .load(headIcon)
                .into(iv_menu_icon)
    }

    override fun setLeftMenuHeadName(name: String) {
        tv_menu_name.text = name
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            when {
                drawer_main.status == DragLayout.Status.Open -> drawer_main.close(true)
                ButtonUtils.isFastDoubleClick() -> finish()
                else -> showMessage(R.string.back_again)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_menu -> drawer_main.open()
            R.id.tv_nav_update -> mPresenter.updateApp()
            R.id.tv_nav_about -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.tv_nav_share -> mPresenter.shareApp()
            R.id.tv_nav_fback -> showMessage("反馈问题（还没写~）")
            R.id.tv_nav_checkout -> mPresenter.checkout()

        }
        if (drawer_main.status == DragLayout.Status.Open) {
            drawer_main.close(true)
        }
    }

    override fun setWeatherText(weather: Pair<String, String>) {
        tv_weather_1.text = weather.first
        tv_weather_2.text = weather.second
    }

    override fun setCourseText(title: String, name: String, address: String, time: String) {
        tv_course_title.text = title
        tv_course_name.text = name
        tv_course_address.text = address
        tv_course_time.text = time
    }

    override fun setTimeLineData(data: List<TimeAxis>) {
        view_timeline.setTimeAxisList(data)
                .invalidate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constant.ACTIVITY_LOGIN && resultCode == Activity.RESULT_OK) {
            mPresenter.addAccountSuccess()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun setCheckoutDialogData(users: List<User>) {
        if (mAccountAdapter == null) {
            checkoutDialog = MaterialDialog(this).apply {
                customView(viewRes = R.layout.dialog_account)
                title(text = "多账号管理")
                negativeButton(text = "添加账号") {
                    openLoginActivity()
                }
            }
            mAccountAdapter = AccountAdapter(users) {
                showAccountDetail(it)
            }
            val rvAccount = checkoutDialog!!.getCustomView().rv_account
            rvAccount.adapter = mAccountAdapter
            rvAccount.layoutManager = LinearLayoutManager(this)
        } else {
            mAccountAdapter?.replaceData(users)
        }
    }

    override fun showCheckoutDialog() {
        checkoutDialog?.show()
    }

    override fun hideCheckoutDialog() {
        checkoutDialog?.hide()
    }

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("from", Constant.ACTIVITY_MAIN)
        startActivityForResult(intent, Constant.ACTIVITY_LOGIN)
    }

    private fun showAccountDetail(user: User) {
        MaterialDialog(this).show {
            title(text = user.name)
            customView(viewRes = R.layout.dialog_account_detail)
            getCustomView().findViewById<EditText>(R.id.et_password).setText(user.password)
            negativeButton(text = "删除账号") {
                mPresenter.deleteUser(user)
            }
            positiveButton(text = "切换账号") {
                mPresenter.checkoutTo(user)
            }
        }
    }

}
