package com.logwiki.specialsurveyservice.domain.message;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageAuthRedisRepository extends CrudRepository<MessageAuth, String> {
    Optional<MessageAuth> findByPhoneNumber(String phoneNumber);
}
