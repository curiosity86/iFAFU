package cn.ifafu.ifafu.experiment.data.service

import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.experiment.bean.IFResponse

/**
 * 通过实现[ZFService]实现不同正方教务管理系统的解析
 */
interface ZFService {

    fun login(user: User): IFResponse<User>

    fun fetchScores(user: User, year: String, term: String): IFResponse<List<Score>>

    fun fetchExams(user: User, year: String, term: String): IFResponse<List<Exam>>
}