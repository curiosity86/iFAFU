package cn.ifafu.ifafu.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.databinding.ActivityTestBinding
import cn.ifafu.ifafu.ui.elective.Elective

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityTestBinding = DataBindingUtil.setContentView(this, R.layout.activity_test)
        val totalScores = listOf(Score())
        binding.elective = Elective("全部选修课，已修${totalScores.size}门",
                "需修满2分，已修2分(已完成)",
                totalScores, true)
    }

}