package io.hhplus.tdd

import io.hhplus.tdd.point.infrastructure.PointHistoryRepository
import io.hhplus.tdd.point.application.PointService
import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.point.infrastructure.UserPointRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
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

    @Test
    fun `포인트 충전을 성공한다`() {
        // Given
        val expectedResult = UserPoint(1, 5000L, System.currentTimeMillis())
        given(userPointRepository.getById(1)).willReturn(UserPoint(1, 0, System.currentTimeMillis()))
        given(userPointRepository.saveOrUpdate(1, 5000L)).willReturn(expectedResult)

        // When
        val acutalResult= sut.charge(1, 5000)

        // Then
        assertThat(acutalResult.id).isEqualTo(expectedResult.id)
        assertThat(acutalResult.point).isEqualTo(expectedResult.point)
    }

    @Test
    fun `충전 금액이 0보다 작거나 같으면 포인트 충전을 실패한다`() {
        // Given
        given(userPointRepository.getById(2)).willReturn(UserPoint(2, 0, System.currentTimeMillis()))

        // When/Then
        assertThrows<RuntimeException> {
            sut.charge(2, 0)
        }

        // When/Then
        assertThrows<RuntimeException> {
            sut.charge(2, -1000)
        }

        // When
        val acutalResult: UserPoint = sut.getPointByUserId(2)
        // Then
        assertThat(acutalResult.point).isEqualTo(0)
    }

    @Test
    fun `포인트 사용을 성공한다` () {
        // Given
        val defaultDate = System.currentTimeMillis();
        val useDate = System.currentTimeMillis();
        val expectedResult = UserPoint(3, 5000L, useDate)
        given(userPointRepository.getById(3)).willReturn(UserPoint(3, 10000, defaultDate))
        given(userPointRepository.saveOrUpdate(3, 5000L)).willReturn(expectedResult)

        // When
        val acutalResult = sut.use(3, 5000L)

        // Then
        assertThat(acutalResult.id).isEqualTo(3)
        assertThat(acutalResult.point).isEqualTo(5000L)
    }

    @Test
    fun `충전된 포인트보다 많은 금액 사용을 요청하면 실패한다` () {
        // Given
        val defaultDate = System.currentTimeMillis()
        given(userPointRepository.getById(4)).willReturn(UserPoint(4, 10000, defaultDate))

        // When/Then
        assertThrows<RuntimeException> {
            sut.use(4, 20000)
        }

        // When
        val result: UserPoint = sut.getPointByUserId(4)
        // Then
        assertThat(result.point).isEqualTo(10000)
    }
}