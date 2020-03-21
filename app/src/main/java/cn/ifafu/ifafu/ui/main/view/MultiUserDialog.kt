package cn.ifafu.ifafu.ui.main.view

import android.content.ClipboardManager
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import cn.ifafu.ifafu.BR
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.databinding.DialogAccountSwitchBinding
import cn.ifafu.ifafu.util.ToastUtils
import cn.woolsen.easymvvm.binding.BindView
import cn.woolsen.easymvvm.binding.BindViews
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class MultiUserDialog(
        private val context: Context,
        private val onAddClick: () -> Unit,
        private val onItemClick: (User) -> Unit
) {

    private val dialog =
            MaterialDialog(context).apply {
                customView(viewRes = R.layout.dialog_account_switch)
                title(text = "多账号管理")
                negativeButton(text = "添加账号") {
                    onAddClick()
                }
                if (BuildConfig.DEBUG) {
                    neutralButton(text = "导入账号") {
                        importAccountFromClipboard()
                    }
                }
            }

    private val binding =
            DataBindingUtil.bind<DialogAccountSwitchBinding>(dialog.getCustomView())

    private var users = emptyList<User>()
    private val tag = HashMap<String, Any>()

    //设置用户数据，延迟初始化
    fun setUsers(users: List<User>) {
        tag["preview_value"] = this.users
        this.users = users
    }

    fun show() {
        if (tag["preview_value"] != this.users) {
            val bindViews = BindViews()
            users.forEach {
                bindViews.add(ItemBindView(
                        when (it.schoolCode) {
                            Constant.FAFU -> R.drawable.fafu_bb_icon_white
                            Constant.FAFU_JS -> R.drawable.fafu_js_icon_white
                            else -> R.drawable.icon_ifafu_round
                        }, it.name, it.account) { onItemClick(it) }
                )
            }
            binding?.bindViews = bindViews
            tag.remove("preview_value")
        }
        dialog.show()
    }

    fun cancel() {
        dialog.cancel()
    }

    private fun importAccountFromClipboard() = GlobalScope.launch {
        try {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val data = cm.primaryClip
            val item = data!!.getItemAt(0)
            val content = item.text.toString()
            val list = JSONObject.parseArray(content, User::class.java)
            list.forEach {
                Repository.user.save(it)
            }
            ToastUtils.showToastShort("导入成功")
        } catch (e: Exception) {
            ToastUtils.showToastShort("导入失败")
        }
    }

    class ItemBindView(
            @DrawableRes
            val schoolIcon: Int,
            val name: String,
            val account: String,
            val click: () -> Unit
    ) : BindView {

        fun onClick() {
            click()
        }

        override fun layoutRes(): Int {
            return R.layout.main_account_recycle_item
        }

        override fun bindingVariable(): Int {
            return BR.data
        }
    }
}