package com.logwiki.specialsurveyservice.api.service.message;

import static org.assertj.core.api.Assertions.assertThat;

import com.logwiki.specialsurveyservice.IntegrationTestSupport;
import com.logwiki.specialsurveyservice.api.service.message.request.LongMessageSendServiceRequest;
import com.logwiki.specialsurveyservice.api.service.message.request.ShortMessageSendServiceRequest;
import com.logwiki.specialsurveyservice.domain.message.Message;
import com.logwiki.specialsurveyservice.api.service.message.request.MultimediaMessageSendServiceRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SendMessageServiceTest extends IntegrationTestSupport {
    @Autowired
    MessageService messageService;

    @DisplayName("SMS(SHORT MESSAGE SERVICE)로 발송을 완료하면 responseCode로 202를 받는다.")
    @Test
    @Disabled("요금 이슈로 테스트 중단")
    void sendSMSTest() {
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .to("01055014037")
                .content("SMS 서비스 테스트")
                .build()
        );
        ShortMessageSendServiceRequest request =
                ShortMessageSendServiceRequest.builder()
                .messages(messages)
                .from("01055014037")
                .content("테스트보내기")
                .build();

        int responseCode = messageService.sendSMS(request);
        assertThat(responseCode).isEqualTo(202);
    }

    @DisplayName("LMS(LONG MESSAGE SERVICE)로 발송을 완료하면 responseCode로 202를 받는다.")
    @Test
    @Disabled("요금 이슈로 테스트 중단")
    void sendLMSTest() {
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder()
                .to("01055014037")
                .content("LMS 서비스 테스트" + "테스트보내기aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                        + "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
                        + "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc")
                .build()
        );
        LongMessageSendServiceRequest request =
                LongMessageSendServiceRequest.builder()
                        .messages(messages)
                        .from("01055014037")
                        .content("공통 LMS")
                        .build();

        int responseCode = messageService.sendLMS(request);
        assertThat(responseCode).isEqualTo(202);
    }


    @DisplayName("MMS(MULTIMEDIA MESSAGE SERVICE)로 발송을 완료하면 responseCode로 202를 받는다.")
    @Test
    @Disabled("요금 이슈로 테스트 중단")
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
        String fileID = messageService.registImageToNCP("더미치킨.jpg",fileBody);
        Message mms = Message.builder()
                .to("01055014037")
                .subject("연재용 MMS 제목")
                .content("연재용 MMS 내용")
                .build();
        List<Message> messages = new ArrayList<>();
        messages.add(mms);
        List<String> fileNames = new ArrayList<>();
        fileNames.add(fileID);
        int responseCode = messageService.sendMMS(MultimediaMessageSendServiceRequest.builder()
                        .from("01055014037")
                        .content("공통MMS내용")
                        .messages(messages)
                        .files(fileNames)
                        .subject("공통 MMS 주제")
                .build());
        assertThat(responseCode).isEqualTo(202);
    }

}
