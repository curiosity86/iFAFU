package cn.ifafu.ifafu.ui.electricity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityElectricityBinding
import cn.ifafu.ifafu.ui.view.LoadingDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import kotlinx.android.synthetic.main.activity_electricity.*

class ElectricityActivity : BaseActivity(), View.OnClickListener {

    
    private val optionPicker: OptionsPickerView<String> by lazy {
        OptionsPickerBuilder(this) { options1, options2, _, _ ->
            viewModel.buildingSelected.postValue(Pair(options1, options2))
        }
                .setSubmitText("确认")
                .setSubmitColor(Color.BLACK)
                .setTitleText("选择校区")
                .isDialog(true)
                .setSelectOptions(0)
                .setOutSideCancelable(false)
                .build<String>()
    }

    private val loginDialog by lazy {
        MaterialDialog(this).apply {
            title(text = "学付宝登录")
            customView(R.layout.dialog_xfb_login)
            val pwdEt = getCustomView().findViewById<EditText>(R.id.et_password)
            val verifyEt = getCustomView().findViewById<EditText>(R.id.et_verify)
            getCustomView().findViewById<Button>(R.id.btn_login).setOnClickListener {
                viewModel.login(pwdEt.text.toString(), verifyEt.text.toString())
            }
            pwdEt.requestFocus()
            setOnCancelListener {
                this@ElectricityActivity.finish()
            }
        }
    }

    private val viewModel: ElectricityViewModel by viewModels { getViewModelFactory() }
    private lateinit var binding: ActivityElectricityBinding
    private var init = false
    private val loadingDialog = LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        binding = bind(R.layout.activity_electricity)
        binding.vm = viewModel
        viewModel.buildingSelected.observe(this, Observer {
            if (!init) {
                init = true
                optionPicker.setSelectOptions(it.first, it.second)
            }
            binding.building = viewModel.buildingSelectionList.value?.second?.get(it.first)?.get(it.second)
        })
        viewModel.verifyBitmap.observe(this, Observer {
            loginDialog.getCustomView().findViewById<ImageView>(R.id.iv_verify).setImageBitmap(it)
        })
        viewModel.buildingSelectionList.observe(this, Observer {
            optionPicker.setPicker(it.first, it.second)
        })
        viewModel.elecUser.observe(this, Observer { user ->
            loginDialog.getCustomView().findViewById<EditText>(R.id.et_verify)?.setText(user.password)
        })
        viewModel.loginStatus.observe(this, Observer {
            if (it) {
                loginDialog.hide()
                viewModel.init()
            } else {
                loginDialog.show()
                viewModel.refreshVerify()
            }
        })
        viewModel.loading.observe(this, Observer {
            if (it == null) {
                loadingDialog.cancel()
            } else {
                loadingDialog.show(it)
            }
        })
        viewModel.init()
        btn_balance.setOnClickListener(this)
        layout_building.setOnClickListener(this)
        tv_fee.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_verify -> viewModel.refreshVerify()
            R.id.btn_balance -> viewModel.queryCardBalance()
            R.id.layout_building -> optionPicker.show()
            R.id.tv_fee -> viewModel.queryElecBalance()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) { //点击editText控件外部
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                if (imm != null) {
                    et_room.clearFocus()
                }
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return window.superDispatchTouchEvent(ev) || onTouchEvent(ev)
    }

    private fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }

}
