package com.laitsystem.zenon_rt_alarm.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class SynologyChatSender {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${synology.chat.webhook-url}")
    private String webhookUrl;

    @Value("${synology.chat.token}")
    private String token;

    public void sendMessage(String message) {
        try {
            String jsonPayload = "{\"text\":\"" + escapeJson(message) + "\"}";
            String encodedPayload = URLEncoder.encode(jsonPayload, StandardCharsets.UTF_8);

            String url = webhookUrl +
                    "?api=SYNO.Chat.External" +
                    "&method=incoming" +
                    "&version=2" +
                    "&token=" + token +
                    "&payload=" + encodedPayload;

            URI uri = new URI(url);

            System.out.println("[SynologyChatSender] 최종 URI: " + uri);

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            System.out.println("[SynologyChatSender] 응답 코드: " + response.getStatusCode().value());
            System.out.println("[SynologyChatSender] 응답 바디: " + response.getBody());

        } catch (Exception e) {
            System.err.println("[SynologyChatSender] 메시지 전송 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }



}

