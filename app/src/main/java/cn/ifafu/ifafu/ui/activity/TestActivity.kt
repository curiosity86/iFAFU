package cn.ifafu.ifafu.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.databinding.ActivityTestBinding
import cn.ifafu.ifafu.ui.main.oldTheme.bean.ClassPreview

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityTestBinding = DataBindingUtil.setContentView(this, R.layout.activity_test)
        binding.semester = "2020-2021学年第1学期课表"
        binding.date = "第1周 5月5日 周三"
        binding.lesson = ClassPreview(
                hasInfo = false,
                nextClassName = "计算机操作系统",
                address = "福建农林大学",
                numberOfClasses = arrayOf(1, 5),
                classTime = "第1节 11:15-12:00",
                timeLeft = "剩余365天上课",
                dateText = "放假中"
        )
    }

}