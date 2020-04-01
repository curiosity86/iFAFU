package cn.ifafu.ifafu.experiment.score

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityScoreBinding
import cn.ifafu.ifafu.util.contentView

class ScoreActivity : BaseActivity() {

    private val binding: ActivityScoreBinding by contentView(R.layout.activity_score)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        DataBindingUtil.setContentView<ActivityScoreBinding>(this, R.layout.activity_score)

    }

}