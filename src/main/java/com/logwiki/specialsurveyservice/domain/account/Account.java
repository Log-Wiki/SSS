package com.logwiki.specialsurveyservice.domain.account;

import com.logwiki.specialsurveyservice.domain.accountauthority.AccountAuthority;
import com.logwiki.specialsurveyservice.domain.sex.Sex;
import com.logwiki.specialsurveyservice.utils.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
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
  private Sex sex;

  private String name;

  private String phoneNumber;

  private int responseSurveyCount;

  private int createSurveyCount;

  private int winningGiveawayCount;

  private int point;

  private LocalDateTime birthday;

  private String refreshToken;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
  private List<AccountAuthority> authorities = new ArrayList<>();
}
