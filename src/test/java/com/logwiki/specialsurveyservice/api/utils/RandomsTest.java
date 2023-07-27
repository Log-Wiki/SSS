package com.logwiki.specialsurveyservice.api.utils;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RandomsTest {

    @DisplayName("숫자 목록에서 랜덤으로 하나의 숫자를 선택한다.")
    @Test
    void pickNumberInList() {
        // given
        List<Integer> numbers = List.of(1, 3, 8, 4, 12, 153, 0, 5001, 10, 413);

        // when // then
        assertThat(numbers.contains(Randoms.pickNumberInList(numbers))).isTrue();
    }

    @DisplayName("숫자 목록이 비어있는 경우 랜덤으로 하나의 숫자를 뽑을 수 없다.")
    @Test
    void pickNumberWithEmptyList() {
        // given
        List<Integer> numbers = List.of();

        // when // then
        assertThatThrownBy(() -> Randoms.pickNumberInList(numbers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("숫자 목록이 비어있습니다.");
    }

    @DisplayName("범위에 있는 값들 중 지정된 개수만큼 랜덤으로 값을 뽑는다.")
    @Test
    void pickUniqueNumbersInRange() {
        // given
        int startInclusive = 0;
        int endInclusive = 100;
        int count = 5;

        // when
        List<Integer> randomNumbers = Randoms.pickUniqueNumbersInRange(startInclusive, endInclusive, count);

        // then
        assertThat(randomNumbers.stream()
                .filter(randomNumber -> randomNumber >= startInclusive && randomNumber <= endInclusive)
                .count())
                .isEqualTo(count);
    }

    @DisplayName("범위 안의 랜덤 값을 추출할 때, 범위의 시작값이 범위의 끝 값 보다 클 수 없다.")
    @Test
    void pickUniqueNumbersInInvalidRange1() {
        // given
        int endInclusive = 100;
        int startInclusive = endInclusive + 10;
        int count = 5;

        // when // then
        assertThatThrownBy(() -> Randoms.pickUniqueNumbersInRange(startInclusive, endInclusive, count))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("범위의 시작값이 범위의 끝 값 보다 클 수 없습니다.");
    }

    @DisplayName("범위 안의 랜덤 값을 추출할 때, 범위의 끝 값은 int의 최대값보다 작아야한다.")
    @Test
    void pickUniqueNumbersInInvalidRange2() {
        // given
        int startInclusive = 1;
        int endInclusive = Integer.MAX_VALUE;
        int count = 5;

        // when // then
        assertThatThrownBy(() -> Randoms.pickUniqueNumbersInRange(startInclusive, endInclusive, count))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("범위의 끝 값이 한계를 초과했습니다.");
    }

    @DisplayName("범위 안의 랜덤 값을 추출할 때, 범위의 크기는 int의 최대값보다 작아야한다.")
    @Test
    void pickUniqueNumbersInInvalidRange3() {
        // given
        int startInclusive = -1;
        int endInclusive = Integer.MAX_VALUE - 1;
        int count = 5;

        // when // then
        assertThatThrownBy(() -> Randoms.pickUniqueNumbersInRange(startInclusive, endInclusive, count))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("범위의 크기가 한계를 초과했습니다.");
    }

    @DisplayName("범위 안의 랜덤 값을 추출할 때, 뽑고 싶은 랜덤 값의 개수는 0보다 커야 한다.")
    @Test
    void pickUniqueNumbersWithZeroCount() {
        // given
        int startInclusive = 1;
        int endInclusive = 10;
        int count = 0;

        // when // then
        assertThatThrownBy(() -> Randoms.pickUniqueNumbersInRange(startInclusive, endInclusive, count))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("뽑을 개수는 0보다 커야 합니다.");
    }

    @DisplayName("범위 안의 랜덤 값을 추출할 때, 뽑고 싶은 랜덤 값의 개수는 범위의 크기보다 작아야한다.")
    @Test
    void pickUniqueNumbersWithOverCount() {
        // given
        int startInclusive = 1;
        int endInclusive = 10;
        int count = endInclusive - startInclusive + 2;

        // when // then
        assertThatThrownBy(() -> Randoms.pickUniqueNumbersInRange(startInclusive, endInclusive, count))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("뽑을 숫자의 개수가 범위의 크기를 초과했습니다.");
    }
}