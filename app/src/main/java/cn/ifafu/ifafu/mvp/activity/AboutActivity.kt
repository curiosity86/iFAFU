package cn.ifafu.ifafu.mvp.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.base.i.IPresenter
import cn.ifafu.ifafu.util.AppUtils
import cn.ifafu.ifafu.util.GlobalLib
import com.afollestad.materialdialogs.MaterialDialog
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.about_activity.*
import java.util.*

class AboutActivity : BaseActivity<IPresenter>(), View.OnClickListener {

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.about_activity
    }

    override fun initData(savedInstanceState: Bundle?) {

        ImmersionBar.with(this)
                .titleBarMarginTop(tb_about)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()

        tb_about.setNavigationOnClickListener { finish() }

        AppUtils.getMetaValue(context, "APP_ICON_HD")?.run {
            iv_app_icon.setImageDrawable(getDrawable(this as Int))
        }
        aboutAppSubName.text = String.format(
                Locale.getDefault(), getString(R.string.app_sub_name),
                GlobalLib.getLocalVersionName(this))
        aboutAppSubName.setOnLongClickListener {
            showMessage("当前迭代版本号：" + GlobalLib.getLocalVersionCode(this).toString())
            true
        }

        btn_feed.setOnClickListener(this)
        btn_goto_qq_group.setOnClickListener(this)
        btn_goto_weibo.setOnClickListener(this)
        btn_goto_email.setOnClickListener(this)
    }

    private fun linkTo(url: String) {
        val intent = Intent()
        intent.data = Uri.parse(url)
        intent.action = Intent.ACTION_VIEW
        startActivity(intent)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_goto_qq_group -> linkTo("https://jq.qq.com/?_wv=1027&k=5BwhG6k")
            R.id.btn_goto_weibo -> linkTo("https://weibo.com/u/5363314862")
            R.id.btn_goto_email -> {
                val email = "support@ifafu.cn"
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                if (cm != null) {
                    cm.setPrimaryClip(ClipData.newPlainText("Label", email))
                    Toast.makeText(this, R.string.success_copy_email, Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(email))
                startActivity(Intent.createChooser(intent, "选择发送应用"))
            }
            R.id.btn_feed -> {
                MaterialDialog(this).show {
                    setContentView(R.layout.about_feed)
                }
            }
        }
    }
}
