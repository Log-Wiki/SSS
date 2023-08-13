package com.logwiki.specialsurveyservice.api.service.surveyresult.response;

import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WinningAccountResponse {

    private Long id;
    private String email;
    private AccountCodeType gender;
    private AccountCodeType age;
    private String name;
    private String phoneNumber;
    private int responseSurveyCount;
    private int createSurveyCount;
    private int winningGiveawayCount;
    private int point;
    private LocalDate birthday;

    @Builder
    private WinningAccountResponse(Long id, String email, AccountCodeType gender, AccountCodeType age,
                            String name, String phoneNumber, int responseSurveyCount, int createSurveyCount,
                            int winningGiveawayCount, int point, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.responseSurveyCount = responseSurveyCount;
        this.createSurveyCount = createSurveyCount;
        this.winningGiveawayCount = winningGiveawayCount;
        this.point = point;
        this.birthday = birthday;
    }

    public static WinningAccountResponse from(Account account) {
        if (account == null) {
            return null;
        }

        return WinningAccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .gender(account.getGender())
                .age(account.getAge())
                .name(account.getName())
                .phoneNumber(account.getPhoneNumber())
                .responseSurveyCount(account.getResponseSurveyCount())
                .createSurveyCount(account.getCreateSurveyCount())
                .winningGiveawayCount(account.getWinningGiveawayCount())
                .point(account.getPoint())
                .birthday(account.getBirthday())
                .build();
    }
}
