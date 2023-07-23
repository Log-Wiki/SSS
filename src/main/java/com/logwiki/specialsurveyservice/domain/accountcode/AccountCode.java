package com.logwiki.specialsurveyservice.domain.accountcode;

import com.logwiki.specialsurveyservice.domain.surveycategory.SurveyCategoryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class AccountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SurveyCategoryType surveyCategoryType;

    @Builder
    public AccountCode(SurveyCategoryType surveyCategoryType) {
        this.surveyCategoryType = surveyCategoryType;
    }
}
