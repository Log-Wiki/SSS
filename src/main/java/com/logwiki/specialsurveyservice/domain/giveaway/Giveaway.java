package com.logwiki.specialsurveyservice.domain.giveaway;

import com.logwiki.specialsurveyservice.api.controller.giveaway.request.GiveawayRequest;
import com.logwiki.specialsurveyservice.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE giveaway SET status = 'INACTIVE' WHERE id = ?")
@Where(clause = "status = 'ACTIVE'")
@Entity
@lombok.Generated
public class Giveaway extends BaseEntity {

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

    public Giveaway update(GiveawayRequest giveawayRequest) {
        this.giveawayType = giveawayRequest.getGiveawayType();
        this.name = giveawayRequest.getName();
        this.price = giveawayRequest.getPrice();
        return this;
    }
}
