package io.hhplus.tdd

import io.hhplus.tdd.point.infrastructure.PointHistoryRepository
import io.hhplus.tdd.point.application.PointService
import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.point.infrastructure.UserPointRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

/************************************************************************************
 * [Test 방식] Mocking vs Stubbing
 * - Mocking: 행위 검증 - 행위를 했는지 안 했는지 여부만 검증할 때 적합
 *            예) 메일 발송, push 알림 => 횟수만으로 검증 가능
 * - Stubbing: 상태 검증 - 입력에 대한 결과 값에 대한 확인이 필요할 때 적합
 ************************************************************************************/

@ExtendWith(MockitoExtension::class)
class PointServiceTest {
    @InjectMocks
    private lateinit var sut: PointService

    @Mock
    private lateinit var userPointRepository: UserPointRepository

    @Mock
    private lateinit var pointHistoryRepository: PointHistoryRepository

    @Test
    fun `포인트 충전을 성공한다`() {
        // given
        val userId1: Long = 1
        val userId2: Long = 2

        // when
        val result1 = sut.charge(userId1, 5000L)
        val result2 = sut.charge(userId2, 99999L)

        // then
        assertThat(result1.id).isEqualTo(userId1)
        assertThat(result2.id).isEqualTo(userId2)
        assertThat(result1.point).isEqualTo(5000)
        assertThat(result2.point).isEqualTo(99999)
    }

    @Test
    fun `충전 금액이 0보다 작거나 같으면 포인트 충전을 실패한다`() {
        // given
        val userId: Long = 3

        // when/Then
        assertThrows<RuntimeException> {
            sut.charge(userId, 0)
        }

        // when/Then
        assertThrows<RuntimeException> {
            sut.charge(userId, -1000)
        }

        // when
        val result: UserPoint = sut.getPointByUserId(userId)
        // then
        assertThat(result.point).isEqualTo(0)
    }

    @Test
    fun `포인트 사용을 성공한다` () {
        // given
        val userId: Long = 4
        sut.charge(userId, 5000L)

        // when
        val result = sut.use(userId, 3000L)

        // Then
        assertThat(result.id).isEqualTo(userId)
        assertThat(result.point).isEqualTo(2000L)
    }

    @Test
    fun `충전된 포인트보다 많은 금액 사용을 요청하면 실패한다` () {
        // given
        val userId: Long = 5
        sut.charge(userId, 5000L)

        // when/Then
        assertThrows<RuntimeException> {
            sut.use(userId, 8000L)
        }

        // 추가 검증
        val result: UserPoint = sut.getPointByUserId(userId)
        // 사용하다 실패 => 원래 금액 5000원
        assertThat(result.point).isEqualTo(5000L)
    }
}