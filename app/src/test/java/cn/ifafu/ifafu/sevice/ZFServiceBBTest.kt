package cn.ifafu.ifafu.sevice

import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.experiment.bean.IFResponse
import cn.ifafu.ifafu.experiment.data.service.ZFService
import cn.ifafu.ifafu.experiment.data.service.ZFServiceBB
import cn.ifafu.ifafu.util.TimberPrintfTree
import org.junit.Before
import org.junit.Test
import timber.log.Timber

class ZFServiceBBTest {

    private lateinit var service: ZFService
    private lateinit var user: User

    @Before
    fun before() {
        Timber.plant(TimberPrintfTree())
        service = ZFServiceBB()
        user = User(account = "3176016051", password = "wkqwkq123", name = "翁凯强", token = "(j44jalzostlhqh552edspk55)")
    }

    @Test
    fun fetchScoresTest() {
        val resp = service.fetchScores(user, "2019-2020", "1")
        when (resp) {
            is IFResponse.Success -> {
                println("ZFServiceBBTest#fetchScoreTest#Success => \n" + resp.data.joinToString("\n") { it.name })
            }
            is IFResponse.Error -> {
                println("ZFServiceBBTest#fetchScoreTest#Error => \n" + resp.exception)
            }
            is IFResponse.Failure -> {
                println("ZFServiceBBTest#fetchScoreTest#Failure => \n" + resp.message)
            }
            is IFResponse.NoAuth -> {
                println("ZFServiceBBTest#fetchScoreTest#NoAuth")
            }
        }
    }

}