package com.logwiki.specialsurveyservice.domain.giveaway;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Giveaway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GiveawayType giveawayType;

    private int price;

    @Builder
    public Giveaway(GiveawayType giveawayType, int price) {
        this.giveawayType = giveawayType;
        this.price = price;
    }
}
