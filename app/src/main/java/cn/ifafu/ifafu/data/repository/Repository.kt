package cn.ifafu.ifafu.data.repository

import cn.ifafu.ifafu.data.IFResult
import cn.ifafu.ifafu.data.entity.Exam

/**
 * get***FromDbOrNet: 若数据库信息为空，则从教务管理系统获取
 */
interface Repository {

    suspend fun getExamsFromDbOrNet(year: String, term: String): IFResult<List<Exam>>
}