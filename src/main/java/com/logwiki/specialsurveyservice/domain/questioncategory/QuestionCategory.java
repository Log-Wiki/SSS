package com.logwiki.specialsurveyservice.domain.questioncategory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class QuestionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuestionCategoryType questionCategoryType;

    @Builder
    public QuestionCategory(QuestionCategoryType questionCategoryType) {
        this.questionCategoryType = questionCategoryType;
    }

}
