package cn.ifafu.ifafu.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityAboutBinding
import cn.ifafu.ifafu.ui.feedback.FeedbackActivity
import cn.ifafu.ifafu.util.AppUtils
import cn.ifafu.ifafu.util.contentView
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseActivity(), View.OnClickListener {

    private val binding: ActivityAboutBinding by contentView(R.layout.activity_about)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        binding.debug = BuildConfig.DEBUG
        binding.version = AppUtils.getVersionName(this)
        aboutAppSubName.setOnLongClickListener {
            Toast.makeText(
                    this,
                    "当前迭代版本号：${AppUtils.getVersionCode(this)}",
                    Toast.LENGTH_SHORT
            ).show()
            true
        }
        btn_feed.setOnClickListener(this)
        btn_goto_qq_group.setOnClickListener(this)
        btn_goto_weibo.setOnClickListener(this)
        btn_goto_email.setOnClickListener(this)
        btn_feedback.setOnClickListener(this)
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
            R.id.btn_feedback -> {
                startActivity(Intent(this, FeedbackActivity::class.java))
            }
            R.id.btn_feed -> {
                MaterialDialog(this).show {
                    setContentView(R.layout.view_about_feed)
                }
            }
        }
    }
}
