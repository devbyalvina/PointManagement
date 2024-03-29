package io.hhplus.tdd.point.infrastructure

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.domain.UserPoint
import org.springframework.stereotype.Repository

@Repository
class UserPointRepositoryImpl (
    private val userPointTable: UserPointTable
) : UserPointRepository {

    override fun getById(id: Long): UserPoint {
        return userPointTable.selectById(id)
    }

    override fun saveOrUpdate(id: Long, amount: Long): UserPoint {
        return userPointTable.insertOrUpdate(id, amount)
    }
}