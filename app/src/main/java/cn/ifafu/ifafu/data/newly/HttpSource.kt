package cn.ifafu.ifafu.data.newly

import cn.ifafu.ifafu.data.IFResult
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.User

interface HttpSource {
    suspend fun switch(user: User)
    suspend fun login(account: String, password: String): IFResult<User>

    /**
     * 获取开学日期
     * @return 2020-02-16
     */
    suspend fun getOpeningDay(): IFResult<String>
    suspend fun getExam(): IFResult<List<Exam>>
}