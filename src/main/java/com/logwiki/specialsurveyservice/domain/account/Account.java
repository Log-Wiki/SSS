package com.logwiki.specialsurveyservice.domain.account;

import com.logwiki.specialsurveyservice.domain.BaseEntity;
import com.logwiki.specialsurveyservice.domain.accountauthority.AccountAuthority;
import com.logwiki.specialsurveyservice.domain.accountcode.AccountCodeType;
import com.logwiki.specialsurveyservice.domain.authority.Authority;
import com.logwiki.specialsurveyservice.domain.surveyresult.SurveyResult;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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
}
