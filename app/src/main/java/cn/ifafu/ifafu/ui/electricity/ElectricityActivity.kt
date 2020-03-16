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
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ElectricityActivityBinding
import cn.ifafu.ifafu.view.dialog.LoadingDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.electricity_activity.*

class ElectricityActivity : BaseActivity<ElectricityActivityBinding, ElectricityViewModel>(), View.OnClickListener {

    private val optionPicker: OptionsPickerView<String> by lazy {
        OptionsPickerBuilder(this) { options1, options2, _, _ ->
            mViewModel.buildingSelected.postValue(Pair(options1, options2))
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
            customView(R.layout.elec_login_dialog)
            getCustomView().findViewById<Button>(R.id.btn_login).setOnClickListener(this@ElectricityActivity)
            setOnCancelListener {
                this@ElectricityActivity.finish()
            }
        }
    }

    private var init = false

    override val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(this).apply {
            setText("查询中")
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.electricity_activity
    }

    override fun getViewModel(): ElectricityViewModel? {
        return VMProvider(this)[ElectricityViewModel::class.java]
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_elec)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        mViewModel.cardBalance.observe(this, Observer { mBinding.cardBalance = it })
        mViewModel.elecBalance.observe(this, Observer { mBinding.feeInfo = it })
        mViewModel.roomData.observe(this, Observer { mBinding.room = it })
        mViewModel.buildingSelected.observe(this, Observer {
            if (!init) {
                init = true
                optionPicker.setSelectOptions(it.first, it.second)
            } else {
                mBinding.feeInfo = getString(R.string.query_elec)
            }
            mBinding.building = mViewModel.buildingSelectionList.value?.second?.get(it.first)?.get(it.second)
        })
        mViewModel.verifyBitmap.observe(this, Observer {
            loginDialog.getCustomView().findViewById<ImageView>(R.id.iv_verify).setImageBitmap(it)
        })
        mViewModel.buildingSelectionList.observe(this, Observer {
            optionPicker.setPicker(it.first, it.second)
        })
        mViewModel.elecUser.observe(this, Observer {
            val verifyEt = loginDialog.getCustomView().findViewById<EditText>(R.id.et_verify)
            verifyEt.setText(it.password)
        })
        mViewModel.loginStatus.observe(this, Observer {
            if (it) {
                loginDialog.hide()
                mViewModel.init()
            } else {
                loginDialog.show()
                mViewModel.refreshVerify()
            }
        })
        mViewModel.init()
        btn_balance.setOnClickListener(this)
        layout_building.setOnClickListener(this)
        tv_fee.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_verify -> mViewModel.refreshVerify()
            R.id.btn_login -> {
                val pwdEt = loginDialog.getCustomView().findViewById<EditText>(R.id.et_password)
                val verifyEt = loginDialog.getCustomView().findViewById<EditText>(R.id.et_verify)
                mViewModel.login(pwdEt.text.toString(), verifyEt.text.toString())
            }
            R.id.btn_balance -> mViewModel.queryCardBalance()
            R.id.layout_building -> optionPicker.show()
            R.id.tv_fee -> mViewModel.queryElecBalance(mBinding.room ?: "")
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
