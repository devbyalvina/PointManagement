package io.hhplus.tdd.point.infrastructure

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType
import org.springframework.stereotype.Repository

@Repository
class PointHistoryRepositoryImpl(
    private val pointHistoryTable: PointHistoryTable
) : PointHistoryRepository {

    override fun getAllByUserId(userId: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(userId)
    }

    override fun save(
        id: Long,
        amount: Long,
        transactionType: TransactionType,
        updateMillis: Long
    ): PointHistory {
        return pointHistoryTable.insert(
            id = id,
            amount = amount,
            transactionType = transactionType,
            updateMillis = updateMillis
        )
    }
}