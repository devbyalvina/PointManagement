package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.springframework.stereotype.Service

@Service
class PointService (
    private val userPointTable: UserPointTable,
    private val pointHistoryTable: PointHistoryTable
) {
    /**
     * 유저 ID로 포인트 조회
     */
    fun getPointByUserId (
        id: Long
    ): UserPoint {
        return userPointTable.selectById(id)
    }

    /**
     * 유저 ID로 포인트 충전/이용 내역 리스트 조회
     */
    fun getPointHistoryListByUserId (
        id: Long
    ): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(id)
    }

    /************************************************************************************
     * charge & use 함수 공통
     * [리팩토링 포인트1] charge & use 메소드의 코드 중복 해결 필요
     *
     * [리팩토링 포인트2] transaction 원자성
     * UserPointTable & PointHistoryTable에 insert/update하는 작업에 대해 transaction 원자성 유지 방법?
     *
     * 1. 각각의 테이블에 대해 rollback 메소드 구현 필요
     * savepoint는 다음과 같음
     * - UserPoint: insert/update 하기 전의 point 값
     * - PointHistoryTable: insert 하기 전의 마지막 커서 값
     *
     * 2. DML 성공/실패 여부 확인 방법
     * 현재 두 테이블에서 insert/update의 결과를 반환해주지 않으므로
     * try/catch로 에러 발생여부로만 DML 성공/실패 여부 체크
     *
     * 3. 에러 발생 케이스에 따른 처리 방법
     * 현재 구현한대로 PointHistoryTable insert -> UserPointTable insert/update 순서로 구현한다고 가정
     * case 1) PointHistoryTable insert에 문제 발생
     *         - rollback할 데이터가 없음
     *         - 그냥 charge 함수 종료
     * case 2) PointHistoryTable insert 성공, UserPointTable insert/update 실패
     *         - PointHistoryTable는 1에서 언급한 savepoint 이후의 데이터는 remove
     *         - UserPointTable는 rollback할 데이터가 없음
     *         - 그리고 charge 함수 종료
     *
     * 이 때 추가 요구사항인 동시성도 고려해야 함
     ************************************************************************************/

    /**
     *  포인트 충전
     */
    fun charge (
        id: Long,
        amount: Long
    ): UserPoint {
        when {
            amount <= 0 -> {
                throw RuntimeException("충전 포인트는 0보다 커야 합니다.")
            }
        }

        val userPoint = userPointTable.selectById(id)
        val result: Long = userPoint.point + amount
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis())
        return userPointTable.insertOrUpdate(userPoint.id, result)
    }

    /**
     *  포인트 사용
     */
    fun use (
        id: Long,
        amount: Long
    ): UserPoint {
        val userPoint = userPointTable.selectById(id)
        when {
            userPoint.point < amount -> {
                throw RuntimeException("사용 가능한 포인트가 부족합니다.")
            }
        }
        val result: Long = userPoint.point - amount
        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis())
        return userPointTable.insertOrUpdate(userPoint.id, result)
    }
}