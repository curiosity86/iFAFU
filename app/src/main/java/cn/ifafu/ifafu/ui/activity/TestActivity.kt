package cn.ifafu.ifafu.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityTestBinding = DataBindingUtil.setContentView(this, R.layout.activity_test)
        val totalScores = listOf(Score())
    }

}