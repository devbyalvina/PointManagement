package io.hhplus.tdd.database

import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType
import org.springframework.stereotype.Component

/**
 * Point History DB Table
 */
@Component
class PointHistoryTable {
    private val table = mutableListOf<PointHistory>()
    private var cursor: Long = 1L

    fun insert(
        id: Long,
        amount: Long,
        transactionType: TransactionType,
        updateMillis: Long,
    ): PointHistory {
        Thread.sleep(Math.random().toLong() * 300L)
        val history = PointHistory(
            id = cursor++,
            userId = id,
            amount = amount,
            type = transactionType,
            timeMillis = updateMillis,
        )
        table.add(history)
        return history
    }

    fun selectAllByUserId(userId: Long): List<PointHistory> {
        return table.filter { it.userId == userId }
    }
}