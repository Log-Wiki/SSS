package com.logwiki.specialsurveyservice.api.service.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.domain.message.Message;
import com.logwiki.specialsurveyservice.api.service.message.request.MessageSendServiceRequest;
import com.logwiki.specialsurveyservice.exception.BaseException;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SendMessageServiceTest extends IntegrationTestSupport {
    @Autowired
    MessageService messageService;

    @DisplayName("MMS 발송")
    @Test
    void sendMMSTest() {
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .to("01055014037")
                .content("SMS 서비스 테스트")
                .build()
        );
        MessageSendServiceRequest request =
                MessageSendServiceRequest.builder()
                .messages(messages)
                .from("01055014037")
                .content("테스트보내기")
                .type("SMS")
                .build();

        int responseCode = messageService.sendSMS(request);
        assertThat(responseCode).isEqualTo(202);
    }

    @DisplayName("메세지 타입은 MMS,LMS,SMS 셋중 하나여야 한다.")
    @Test
    void MMSTypeTest() {
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .to("01055014037")
                .content("SMS 서비스 테스트")
                .build()
        );
        MessageSendServiceRequest request =
                MessageSendServiceRequest.builder()
                        .messages(messages)
                        .from("01055014037")
                        .content("테스트보내기")
                        .type("KMS")
                        .build();

        assertThatThrownBy(() ->messageService.sendSMS(request))
                .isInstanceOf(BaseException.class)
                .hasMessage("메세지 타입이 SMS가 아닙니다.");
    }

    @DisplayName("MMS 전송 테스트")
    @Test
    void registImageTest() {
        String fileBody = "";
        File f = new File("src/test/resources/static.img/dummyGift.jpg");
        if(f.exists() && f.isFile() && f.length() > 0) {
            byte[] bt = new byte[(int)f.length()];
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                fis.read(bt);
                fileBody = new String(Base64.getEncoder().encodeToString(bt));
            } catch (Exception e) {

            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                }
                catch (Exception e) {

                }
            }
        }
        System.out.println("사진 : " + fileBody);
        String fileID = messageService.registImageToNCP("더미치킨.jpg",fileBody);
        System.out.println("사진등록 " + fileID);
        Message mms = Message.builder()
                .to("01055014037")
                .subject("연재용 MMS 제목")
                .content("연재용 MMS 내용")
                .build();
        List<Message> messages = new ArrayList<>();
        messages.add(mms);
        List<String> fileNames = new ArrayList<>();
        fileNames.add(fileID);
        messageService.sendMMS(MessageSendServiceRequest.builder()
                        .type("MMS")
                        .from("01055014037")
                        .content("공통MMS내용")
                        .messages(messages)
                        .files(fileNames)
                        .subject("공통 MMS 주제")
                .build());
    }

}
