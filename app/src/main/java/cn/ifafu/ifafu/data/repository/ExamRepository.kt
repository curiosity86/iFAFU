package cn.ifafu.ifafu.data.repository

import cn.ifafu.ifafu.data.entity.Exam

interface ExamRepository {
    suspend fun getAllFromNet(): List<Exam>
    suspend fun getAllByYearAndTermFromNet(year: String, term: String): List<Exam>

    suspend fun getAllFromDb(): List<Exam>
    suspend fun getAllByYearAndTermFromDb(year: String, term: String): List<Exam>

    suspend fun save(exams: List<Exam>)
}