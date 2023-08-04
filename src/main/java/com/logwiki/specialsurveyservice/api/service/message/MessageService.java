package com.logwiki.specialsurveyservice.api.service.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logwiki.specialsurveyservice.api.service.message.request.LongMessageSendServiceRequest;
import com.logwiki.specialsurveyservice.api.service.message.request.ShortMessageSendServiceRequest;
import com.logwiki.specialsurveyservice.domain.message.Message;
import com.logwiki.specialsurveyservice.api.service.message.request.MultimediaMessageSendServiceRequest;
import com.logwiki.specialsurveyservice.domain.message.MessageType;
import com.logwiki.specialsurveyservice.exception.BaseException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URL;

@Slf4j
@Service
public class MessageService {
    private final static int SUCCESS = 202;
    private final static int DEFAULT = 0;
    @Value("${apikey.naver-accesskey}")
    private String accessKey;
    @Value("${apikey.naver-secretkey}")
    private String secretKey;
    @Value("${apikey.naver-message-servickey}")
    private String messageServiceKey;
    private String makeSignature(String url, String timestamp, String method)
    {
        String space = " ";					// one space
        String newLine = "\n";					// new line

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();
        byte[] rawHmac = null;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        }
        catch (Exception e) {
            throw new BaseException("네이버클라우드 문자 API 키생성 오류",8000);
        }
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

   public int sendSMS(ShortMessageSendServiceRequest request) {
       int responseCode = DEFAULT;
       String timestamp = Long.toString(System.currentTimeMillis());
       String hostNameUrl = "https://sens.apigw.ntruss.com";
       String requestUrl = "/sms/v2/services/"+ messageServiceKey  + "/messages";
       String method = "POST";

       JSONObject bodyJson = new JSONObject();

       JSONArray toArr = new JSONArray();

       bodyJson.put("type" , request.getType().getText());
       bodyJson.put("from", request.getFrom());
       bodyJson.put("content",request.getContent());

       for(Message message : request.getMessages()) {
               JSONObject target = new JSONObject();
               target.put("to",message.getTo());
               target.put("content",message.getContent());
               toArr.add(target);
       }

       bodyJson.put("messages",toArr);

       String body = bodyJson.toString();
       try {
           URL url = new URL(hostNameUrl + requestUrl);
           HttpURLConnection conn = (HttpURLConnection) url.openConnection();
           conn.setUseCaches(false);
           conn.setDoInput(true);
           conn.setDoOutput(true);
           conn.setRequestProperty("Content-Type","application/json");
           conn.setRequestProperty("x-ncp-apigw-timestamp",timestamp);
           conn.setRequestProperty("x-ncp-iam-access-key",accessKey);
           conn.setRequestProperty("x-ncp-apigw-signature-v2",makeSignature(requestUrl,timestamp,method));
           conn.setRequestMethod(method);
           DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
           dos.write(body.getBytes());
           dos.flush();
           dos.close();
           responseCode = conn.getResponseCode();
           if(responseCode != SUCCESS) {
               throw new BaseException("설문 요청 결과가 성공이 아닙니다.",8003);
           }

       }
       catch (IOException e1) {
           throw new BaseException("문자 발송 API 요청 오류",8002);
       }
       catch (Exception e) {
            throw new BaseException("문자 발송 API 요청 오류",8001);
       }

       return responseCode;
   }

    public int sendLMS(LongMessageSendServiceRequest request) {
        int responseCode = DEFAULT;
        String timestamp = Long.toString(System.currentTimeMillis());
        String hostNameUrl = "https://sens.apigw.ntruss.com";
        String requestUrl = "/sms/v2/services/"+ messageServiceKey  + "/messages";
        String method = "POST";

        JSONObject bodyJson = new JSONObject();

        JSONArray toArr = new JSONArray();

        bodyJson.put("type" , request.getType().getText());
        bodyJson.put("from", request.getFrom());
        bodyJson.put("content",request.getContent());

        for(Message message : request.getMessages()) {
            JSONObject target = new JSONObject();
            target.put("to",message.getTo());
            target.put("content",message.getContent());
            toArr.add(target);
        }

        bodyJson.put("messages",toArr);

        String body = bodyJson.toString();
        try {
            URL url = new URL(hostNameUrl + requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("x-ncp-apigw-timestamp",timestamp);
            conn.setRequestProperty("x-ncp-iam-access-key",accessKey);
            conn.setRequestProperty("x-ncp-apigw-signature-v2",makeSignature(requestUrl,timestamp,method));
            conn.setRequestMethod(method);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.write(body.getBytes());
            dos.flush();
            dos.close();
            responseCode = conn.getResponseCode();
            if(responseCode != SUCCESS) {
                throw new BaseException("설문 요청 결과가 성공이 아닙니다.",8003);
            }

        }
        catch (IOException e1) {
            throw new BaseException("문자 발송 API 요청 오류",8002);
        }
        catch (Exception e) {
            throw new BaseException("문자 발송 API 요청 오류",8001);
        }

        return responseCode;
    }

   public int sendMMS(MultimediaMessageSendServiceRequest request) {
       int responseCode = DEFAULT;
       String timestamp = Long.toString(System.currentTimeMillis());
       String hostNameUrl = "https://sens.apigw.ntruss.com";
       String requestUrl = "/sms/v2/services/"+ messageServiceKey  + "/messages";
       String method = "POST";

       JSONObject bodyJson = new JSONObject();

       JSONArray toArr = new JSONArray();

       bodyJson.put("type" , request.getType().getText());
       bodyJson.put("from", request.getFrom());
       bodyJson.put("content",request.getContent());
       bodyJson.put("subject",request.getSubject());

       for(Message message : request.getMessages()) {
           JSONObject toJson = new JSONObject();
           toJson.put("to",message.getTo());
           toJson.put("content",message.getContent());
           toJson.put("subject",message.getSubject());
           toArr.add(toJson);
       }

       JSONArray fileArr = new JSONArray();
       for(String fileId : request.getFiles()) {
           JSONObject fileIdJson = new JSONObject();
           fileIdJson.put("fileId", fileId);
           fileArr.add(fileIdJson);
       }
       bodyJson.put("files" , fileArr);

       bodyJson.put("messages",toArr);

       String body = bodyJson.toString();
       try {
           URL url = new URL(hostNameUrl + requestUrl);
           HttpURLConnection conn = (HttpURLConnection) url.openConnection();
           conn.setUseCaches(false);
           conn.setDoInput(true);
           conn.setDoOutput(true);
           conn.setRequestProperty("Content-Type","application/json");
           conn.setRequestProperty("x-ncp-apigw-timestamp",timestamp);
           conn.setRequestProperty("x-ncp-iam-access-key",accessKey);
           conn.setRequestProperty("x-ncp-apigw-signature-v2",makeSignature(requestUrl,timestamp,method));
           conn.setRequestMethod(method);
           DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
           dos.write(body.getBytes());
           dos.flush();
           dos.close();
           responseCode = conn.getResponseCode();
           if(responseCode != SUCCESS) {
               throw new BaseException("설문 요청 결과가 성공이 아닙니다.",8003);
           }

       }
       catch (IOException e1) {
           e1.printStackTrace();
           throw new BaseException("문자 발송 API 요청 오류",8002);
       }
       catch (Exception e) {
           throw new BaseException("문자 발송 API 요청 오류",8001);
       }
       return responseCode;
   }
   public String registImageToNCP(String fileName , String fileBody) {
       int responseCode = DEFAULT;
       String fileId = null;
       String timestamp = Long.toString(System.currentTimeMillis());
       String hostNameUrl = "https://sens.apigw.ntruss.com";
       String requestUrl = "/sms/v2/services/"+ messageServiceKey + "/files";
       String method = "POST";

       JSONObject bodyJson = new JSONObject();

       bodyJson.put("fileName" , fileName);
       bodyJson.put("fileBody", fileBody);


       String body = bodyJson.toString();
       try {
           URL url = new URL(hostNameUrl + requestUrl);
           HttpURLConnection conn = (HttpURLConnection) url.openConnection();
           conn.setUseCaches(false);
           conn.setDoInput(true);
           conn.setDoOutput(true);
           conn.setRequestProperty("Content-Type","application/json");
           conn.setRequestProperty("x-ncp-apigw-timestamp",timestamp);
           conn.setRequestProperty("x-ncp-iam-access-key",accessKey);
           conn.setRequestProperty("x-ncp-apigw-signature-v2",makeSignature(requestUrl,timestamp,method));
           conn.setRequestMethod(method);
           DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
           dos.write(body.getBytes());
           dos.flush();
           dos.close();
           responseCode = conn.getResponseCode();
           if(responseCode != 200) {
               throw new BaseException("이미지 업로드 요청결과가 성공이 아닙니다.",8003);
           }
           BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
           String inputLine;
           StringBuffer sb = new StringBuffer();
           while((inputLine = br.readLine()) != null) {
               sb.append(inputLine);
           }
           br.close();
           ObjectMapper objectMapper = new ObjectMapper();
           JsonNode jsonNode = objectMapper.readTree(sb.toString());

           fileId = (String) jsonNode.get("fileId").asText();
       }
       catch (IOException ioException) {
           throw new BaseException("사진 등록 API 요청 오류",8002);
       }
       catch (Exception exception) {
           throw new BaseException(exception.getMessage(),8003);
       }

       return fileId;
   }


}
