package com.logwiki.specialsurveyservice.domain.multiplechoice;

import com.logwiki.specialsurveyservice.domain.question.Question;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@lombok.Generated
public class MultipleChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Long linkNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Builder
    public MultipleChoice(String content, Long linkNumber, Question question) {
        this.content = content;
        this.linkNumber = linkNumber;
        this.question = question;
    }

    public void addQuestion(Question question) {
        this.question = question;
    }
}
