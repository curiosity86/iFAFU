package cn.ifafu.ifafu.data.repository.impl

import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.repository.ExamRepository
import cn.ifafu.ifafu.data.repository.ParamsRepository
import javax.inject.Inject

class ExamRepositoryImpl @Inject constructor(
        private val paramsRepository: ParamsRepository
) : ExamRepository {

    override suspend fun getAllFromNet(): List<Exam> {
        return emptyList()
    }

    override suspend fun getAllByYearAndTermFromNet(year: String, term: String): List<Exam> {
        return emptyList()
    }

    override suspend fun getAllFromDb(): List<Exam> {
        return emptyList()
    }

    override suspend fun getAllByYearAndTermFromDb(year: String, term: String): List<Exam> {
        return emptyList()
    }

    override suspend fun save(exams: List<Exam>) {

    }

}