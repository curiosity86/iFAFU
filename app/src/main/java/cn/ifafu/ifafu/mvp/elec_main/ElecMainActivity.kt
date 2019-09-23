package cn.ifafu.ifafu.mvp.elec_main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.mvp.elec_login.ElecLoginActivity
import cn.ifafu.ifafu.view.dialog.ProgressDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.checkbox.isCheckPromptChecked
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_elec_main.*
import kotlinx.android.synthetic.main.include_elec_main_balance.*
import kotlinx.android.synthetic.main.include_elec_main_elec.*

class ElecMainActivity : BaseActivity<ElecMainContract.Presenter>(), ElecMainContract.View, RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private var progress: ProgressDialog? = null

    private var viewIdToNameMap: SparseArray<String> = SparseArray()  //记录RadioButtonId对应的电控名字

    private var xqOpv: OptionsPickerView<String>? = null
    private var ldOpv: OptionsPickerView<String>? = null
    private var lcOpv: OptionsPickerView<String>? = null

    private var xqData: List<String>? = null
    private var ldData: List<String>? = null
    private var lcData: List<String>? = null

    private lateinit var confirmDialog: MaterialDialog

    private var lastRoomText = ""

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.activity_elec_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_elec)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()

        mPresenter = ElecMainPresenter(this)

        viewIdToNameMap = SparseArray()

        progress = ProgressDialog(this)
        progress!!.setText("加载中")

        radioGroup.setOnCheckedChangeListener(this)

        xqOpv = OptionsPickerBuilder(this) { options1, _, _, _ ->
            mPresenter.onAreaSelect(xqData!![options1])
            tv_area.text = xqData!![options1]
        }
                .setSubmitText("确认")
                .setSubmitColor(Color.BLACK)
                .setTitleText("选择校区")
                .isDialog(true)
                .setSelectOptions(0)
                .setOutSideCancelable(false)
                .build()

        ldOpv = OptionsPickerBuilder(this) { options1, _, _, _ ->
            mPresenter.onBuildingSelect(ldData!![options1])
            tv_building.text = ldData!![options1]
        }
                .setSubmitText("确认")
                .setSubmitColor(Color.BLACK)
                .setTitleText("选择楼栋")
                .isDialog(true)
                .setSelectOptions(0)
                .setOutSideCancelable(false)
                .build()

        lcOpv = OptionsPickerBuilder(this) { options1, _, _, _ ->
            mPresenter.onFloorSelect(lcData!![options1])
            tv_floor.text = lcData!![options1]
        }
                .setSubmitText("确认")
                .setSubmitColor(Color.BLACK)
                .setTitleText("选择楼层")
                .isDialog(true)
                .setSelectOptions(0)
                .setOutSideCancelable(false)
                .build()

        et_room.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && lastRoomText != et_room.text.toString()) {
                tv_elec.setText(R.string.query_elec)
                et_price.visibility = View.GONE
                et_price.setText("")
                btn_pay!!.visibility = View.GONE
            }
        }

        initViewVisibility()

        confirmDialog = MaterialDialog(this).apply {
            noAutoDismiss()
            checkBoxPrompt(text = "我知道了") {}
            positiveButton(text = "确定") {
                if (it.isCheckPromptChecked()) {
                    mPresenter.pay()
                    it.dismiss()
                } else {
                    showMessage("请确认注意事项")
                }
            }
            negativeButton(text = "取消") {
                it.dismiss()
            }
        }

        balanceBtn.setOnClickListener(this)
        tv_area.setOnClickListener(this)
        tv_building.setOnClickListener(this)
        tv_floor.setOnClickListener(this)
        tv_elec.setOnClickListener(this)
        btn_pay.setOnClickListener(this)
    }

    override fun setBalanceText(text: String) {
        balanceTV.text = getString(R.string.balance, text)
    }

    override fun setSelectorView(i: Int, list: List<String>) {
        if (i == 1) {
            tv_area!!.visibility = View.VISIBLE
            xqData = list
            xqOpv!!.setPicker(list)
        } else if (i == 2) {
            tv_building.visibility = View.VISIBLE
            ldData = list
            ldOpv!!.setPicker(list)
        } else if (i == 3) {
            tv_floor.visibility = View.VISIBLE
            lcData = list
            lcOpv!!.setPicker(list)
        }
    }

    override fun setSelections(dkName: String, area: String, building: String, floor: String) {
        radioGroup!!.check(viewIdToNameMap.keyAt(viewIdToNameMap.indexOfValue(dkName)))
        if (area.isNotEmpty()) {
            tv_area.text = area
            tv_area.visibility = View.VISIBLE
        }
        if (building.isNotEmpty()) {
            tv_building.text = building
            tv_building.visibility = View.VISIBLE
        }
        if (floor.isNotEmpty()) {
            tv_floor.text = floor
            tv_floor.visibility = View.VISIBLE
        }
    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        if (viewIdToNameMap.get(checkedId) != null) {
            mPresenter.onDKSelect(viewIdToNameMap.get(checkedId))
        }
    }

    override fun initViewVisibility() {
        tv_area.visibility = View.GONE
        tv_area.text = ""
        tv_building.visibility = View.GONE
        tv_building.text = ""
        tv_floor.visibility = View.GONE
        tv_floor.text = ""
        et_room.visibility = View.GONE
        et_room.setText("")
        tv_elec.visibility = View.GONE
        tv_elec.text = ""
        et_price.visibility = View.GONE
        et_price.setText("")
        btn_pay.visibility = View.GONE
    }

    override fun setElecText(text: String) {
        tv_elec.text = text
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.balanceBtn -> mPresenter.queryCardBalance()
            R.id.tv_area -> xqOpv!!.show()
            R.id.tv_building -> ldOpv!!.show()
            R.id.tv_floor -> lcOpv!!.show()
            R.id.tv_elec -> mPresenter.queryElecBalance()
            R.id.btn_pay -> {
                MaterialDialog(this).show {
                    title(text = "致歉")
                    message(text = "非常抱歉，由于学付宝返回数据不统一，导致充值电费功能存在问题，所以" +
                            "iFAFU充值电费功能暂时下架。需要充值电费的同学自行下载学付宝充值。对此表示非常抱歉。")
                    positiveButton(text = "嗯")
                }
//                mPresenter.whetherPay()
            }

        }
    }

    override fun showLoading() {
        progress!!.show()
    }

    override fun showPayView() {
        et_price.visibility = View.VISIBLE
        btn_pay!!.visibility = View.VISIBLE
    }

    override fun showConfirmDialog(message: String) {
        confirmDialog.message(text = message)
        confirmDialog.show()
    }

    override fun showElecCheckView() {
        et_room.visibility = View.VISIBLE
        tv_elec.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress!!.cancel()
    }

    override fun showDKSelection(strings: Collection<String>) {
        for (s in strings) {
            val rb1 = RadioButton(this)
            val rgLp = RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            )
            rb1.layoutParams = rgLp
            rb1.append(s)
            val id = View.generateViewId()
            rb1.id = id
            viewIdToNameMap.append(id, s)
            radioGroup!!.addView(rb1)
        }
    }

    override fun setRoomText(text: String) {
        lastRoomText = text
        et_room.setText(text)
    }

    override fun getCheckedDKName(): String {
        return viewIdToNameMap.get(radioGroup!!.checkedRadioButtonId)
    }

    override fun getAreaText(): String {
        return tv_area!!.text.toString()
    }

    override fun getBuildingText(): String {
        return tv_building!!.text.toString()
    }

    override fun getFloorText(): String {
        return tv_floor!!.text.toString()
    }

    override fun getPriceText(): String {
        return et_price.text.toString()
    }

    override fun getRoomText(): String {
        return et_room.text.toString()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {//点击editText控件外部
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

    override fun openLoginActivity() {
        startActivity(Intent(this, ElecLoginActivity::class.java))
        finish()
    }

}
