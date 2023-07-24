package com.logwiki.specialsurveyservice.api.service.question.response;

import com.logwiki.specialsurveyservice.domain.multiplechoice.MultipleChoice;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MultipleChoiceResponse {

  private Long id;

  private String content;

  private Long linkNumber;


  @Builder
  public MultipleChoiceResponse(Long id, String content, Long linkNumber) {
    this.id = id;
    this.content = content;
    this.linkNumber = linkNumber;
  }

  public static MultipleChoiceResponse from(MultipleChoice multipleChoice) {
    if (multipleChoice == null) {
      return null;
    }

    return MultipleChoiceResponse.builder()
        .id(multipleChoice.getId())
        .content(multipleChoice.getContent())
        .linkNumber(multipleChoice.getLinkNumber())
        .build();
  }

}
