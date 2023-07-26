package com.logwiki.specialsurveyservice.api.service.userdetail.response;

import com.logwiki.specialsurveyservice.domain.account.Account;
import com.logwiki.specialsurveyservice.domain.gender.Gender;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDetailResponse {

    private Long id;
    private String email;
    private Gender gender;
    private String name;
    private String phoneNumber;
    private int responseSurveyCount;
    private int createSurveyCount;
    private int winningGiveawayCount;
    private int point;
    private LocalDate birthday;
    private String refreshToken;

    @Builder
    private UserDetailResponse(Long id, String email, Gender gender, String name, String phoneNumber, int responseSurveyCount, int createSurveyCount, int winningGiveawayCount, int point, LocalDate birthday, String refreshToken) {
        this.id = id;
        this.email = email;
        this.gender = gender;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.responseSurveyCount = responseSurveyCount;
        this.createSurveyCount = createSurveyCount;
        this.winningGiveawayCount = winningGiveawayCount;
        this.point = point;
        this.birthday = birthday;
        this.refreshToken = refreshToken;
    }

    public static UserDetailResponse from(Account account) {
        if (account == null) {
            return null;
        }

        return UserDetailResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .gender(account.getGender())
                .name(account.getName())
                .phoneNumber(account.getPhoneNumber())
                .responseSurveyCount(account.getResponseSurveyCount())
                .createSurveyCount(account.getCreateSurveyCount())
                .winningGiveawayCount(account.getWinningGiveawayCount())
                .point(account.getPoint())
                .birthday(account.getBirthday())
                .refreshToken(account.getRefreshToken())
                .build();
    }
}
