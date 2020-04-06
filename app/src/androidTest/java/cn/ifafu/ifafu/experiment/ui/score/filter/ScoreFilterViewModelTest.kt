package cn.ifafu.ifafu.experiment.ui.score.filter

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test

class ScoreFilterViewModelTest {

    private lateinit var viewMode: ScoreFilterViewModel

    @Before
    fun before() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        viewMode = ScoreFilterViewModel()
    }

    @Test
    fun getScores() {
    }

    @Test
    fun setScores() {
    }

    @Test
    fun getIes() {
    }

    @Test
    fun init() {
    }

    @Test
    fun itemChecked() {
    }

    @Test
    fun allChecked() {
    }
}