package com.logwiki.specialsurveyservice.domain.account;

import com.logwiki.specialsurveyservice.api.controller.account.request.AccountUpdateRequest;
import com.logwiki.specialsurveyservice.domain.BaseEntity;
import com.logwiki.specialsurveyservice.domain.accountauthority.AccountAuthority;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.accountsurvey.AccountSurvey;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE account SET status = 'INACTIVE' WHERE id = ?")
@Where(clause = "status = 'ACTIVE'")
@Entity
@lombok.Generated
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private AccountCodeType gender;

    @Enumerated(EnumType.STRING)
    private AccountCodeType age;

    private String name;

    private String phoneNumber;

    private int responseSurveyCount;

    private int createSurveyCount;

    private int winningGiveawayCount;

    private int point;

    private LocalDate birthday;

    private String refreshToken;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<AccountAuthority> authorities = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<SurveyResult> surveyResults = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccountSurvey> accountSurveys = new ArrayList<>();

    @Builder
    public Account(String email, String password,
            AccountCodeType gender, AccountCodeType age, String name,
            String phoneNumber, LocalDate birthday,
            List<Authority> authorities) {
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.age = age;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.authorities = authorities.stream()
                .map(authority -> new AccountAuthority(this, authority))
                .collect(Collectors.toList());
    }

    public static Account create(String email, String password,
            AccountCodeType gender, AccountCodeType age,
            String name, String phoneNumber, LocalDate birthday,
            List<Authority> authorities) {
        return Account.builder()
                .email(email)
                .password(password)
                .gender(gender)
                .age(age)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .authorities(authorities)
                .build();
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void increaseResponseSurveyCount() {
        this.responseSurveyCount++;
    }

    public void increaseWinningGiveawayCount() {
        this.winningGiveawayCount++;
    }

    public void increaseCreateSurveyCount() {
        this.createSurveyCount++;
    }

    public Account update(AccountUpdateRequest accountUpdateRequest) {
        if(accountUpdateRequest.getPassword() != null) {
            this.password = accountUpdateRequest.getPassword();
        }
        if(accountUpdateRequest.getPhoneNumber() != null) {
            this.phoneNumber = accountUpdateRequest.getPhoneNumber();
        }
        if(accountUpdateRequest.getName() != null) {
            this.name = accountUpdateRequest.getName();
        }
        return this;
    }

    public void addAccountSurvey(AccountSurvey accountSurvey) {
        accountSurveys.add(accountSurvey);
    }
}
