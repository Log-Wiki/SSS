package com.logwiki.specialsurveyservice.api.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Randoms {

    private static final Random defaultRandom = ThreadLocalRandom.current();

    private Randoms() {
    }

    public static int pickNumberInList(final List<Integer> numbers) {
        validateNumbers(numbers);
        return numbers.get(pickNumberInRange(0, numbers.size() - 1));
    }

    public static int pickNumberInRange(final int startInclusive, final int endInclusive) {
        validateRange(startInclusive, endInclusive);
        return startInclusive + defaultRandom.nextInt(endInclusive - startInclusive + 1);
    }

    public static List<Integer> pickUniqueNumbersInRange(
            final int startInclusive,
            final int endInclusive,
            final int count
    ) {
        validateRange(startInclusive, endInclusive);
        validateCount(startInclusive, endInclusive, count);
        final List<Integer> numbers = new ArrayList<>();
        for (int i = startInclusive; i <= endInclusive; i++) {
            numbers.add(i);
        }
        return shuffle(numbers).subList(0, count);
    }

    public static <T> List<T> shuffle(final List<T> list) {
        final List<T> result = new ArrayList<>(list);
        Collections.shuffle(result);
        return result;
    }

    private static void validateNumbers(final List<Integer> numbers) {
        if (numbers.isEmpty()) {
            throw new IllegalArgumentException("숫자 목록이 비어있습니다.");
        }
    }

    private static void validateRange(final int startInclusive, final int endInclusive) {
        if (startInclusive > endInclusive) {
            throw new IllegalArgumentException("범위의 시작값이 범위의 끝 값 보다 클 수 없습니다.");
        }
        if (endInclusive == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("범위의 끝 값이 한계를 초과했습니다.");
        }
        if (endInclusive - startInclusive >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("범위의 크기가 한계를 초과했습니다.");
        }
    }

    private static void validateCount(final int startInclusive, final int endInclusive, final int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("뽑을 개수는 0보다 커야 합니다.");
        }
        if (endInclusive - startInclusive + 1 < count) {
            throw new IllegalArgumentException("뽑을 숫자의 개수가 범위의 크기를 초과했습니다.");
        }
    }
}
