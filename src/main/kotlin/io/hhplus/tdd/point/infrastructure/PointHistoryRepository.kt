package io.hhplus.tdd.point.infrastructure

import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType

interface PointHistoryRepository {
    fun save(id: Long, amount: Long, transactionType: TransactionType, updateMillis: Long): PointHistory

    fun getAllByUserId(userId: Long): List<PointHistory>
}