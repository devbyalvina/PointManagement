package io.hhplus.tdd.point.infrastructure

import io.hhplus.tdd.point.domain.UserPoint

interface UserPointRepository {
    fun getById(id: Long): UserPoint

    fun saveOrUpdate(id: Long, amount: Long): UserPoint
}