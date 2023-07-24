package com.logwiki.specialsurveyservice.domain.giveaway;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiveawayRepository extends JpaRepository<Giveaway, Long> {

    Optional<Giveaway> findGiveawayByName(String name);
}
