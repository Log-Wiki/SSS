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

    private String name;

    private int price;

    @Builder
    public Giveaway(GiveawayType giveawayType, String name, int price) {
        this.giveawayType = giveawayType;
        this.name = name;
        this.price = price;
    }
}
