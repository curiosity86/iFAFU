package cn.ifafu.ifafu

import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.data.Api
import cn.ifafu.ifafu.http.RetrofitManager
import cn.ifafu.ifafu.http.service.ZhengFangService2
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.*

class ExampleUnitTest2 {

    private val zhengFang = RetrofitManager.obtainService(ZhengFangService2::class.java, "http://jwgl.fafu.edu.cn")

    @Test
    fun test() {

        ProxyUtil.setLocalProxy()

        val account = "3176016051"
        val password = "wkqwkq1234"

        val api = Api("http://jwgl.fafu.edu.cn/")

        Observable.fromCallable { zhengFang.getByUrl(api.baseUrl).execute() }
                .subscribe {
                    api.addApi(Constant.API_LOGIN, it.headers().get("Location").toString())
                    println(api)
                    println("message: " + it.message())
                    println("isSuccessful: " + it.isSuccessful.toString())
                    println("code: " + it.code())
                    println("headers: " + it.headers().toMultimap())
                    println("body: " + it.body()?.string())
                    println()
                }

        Observable.fromCallable { zhengFang.login(api[Constant.API_LOGIN], account, password, "",
                "", "").execute() }
                .subscribe {
                    println("message: " + it.message())
                    println("isSuccessful: " + it.isSuccessful.toString())
                    println("code: " + it.code())
                    println("headers: " + it.headers().toString())
                    println("body: " + it.body()?.string())
                }
        val s = Scanner(System.`in`)
        s.next()
    }

    fun getData(): String {
        p("getData")
        return "123";
    }

    fun p(string: String) {
        println(("""${Thread.currentThread()}    $string"""))
    }

}