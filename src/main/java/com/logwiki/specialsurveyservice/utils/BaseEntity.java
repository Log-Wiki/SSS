package com.logwiki.specialsurveyservice.utils;


import com.logwiki.specialsurveyservice.status.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

  @CreatedDate
  private LocalDateTime createAt;

  @LastModifiedDate
  private LocalDateTime modifiedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
  private Status status = Status.ACTIVE;

  @PrePersist
  public void prePersist() {
    this.createAt = LocalDateTime.now();
    this.modifiedAt = LocalDateTime.now();
    this.status = Status.ACTIVE;
  }

  @PreUpdate
  public void preUpdate() {
    this.modifiedAt = LocalDateTime.now();
  }
}
