package com.laitsystem.zenon_rt_alarm.controller;

import com.laitsystem.zenon_rt_alarm.component.SynologyChatSender;
import com.laitsystem.zenon_rt_alarm.dto.HeartbeatPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



@RestController
@RequestMapping("/api")
public class HeartbeatController {

    @Autowired
    private SynologyChatSender chatSender;

    private final Map<String, Instant> lastHeartbeatMap = new ConcurrentHashMap<>();
    private final Map<String, Instant> firstHeartbeatMap = new ConcurrentHashMap<>();

    private final Map<String, HeartbeatPayload> heartbeatDataMap = new ConcurrentHashMap<>();

    @GetMapping("/statusBatch")
    public ResponseEntity<?> statusBatch(@RequestParam String clientIds) {
        Instant now = Instant.now();
        String[] ids = clientIds.split(","); // 요청된 클라이언트 ID 문자열을 분리

        Map<String, Object> batchStatus = new ConcurrentHashMap<>();
        for (String clientId : ids) {
            if (!lastHeartbeatMap.containsKey(clientId)) {
                batchStatus.put(clientId, "Client not found");
            } else {
                batchStatus.put(clientId, buildStatus(clientId, now));
            }
        }

        return ResponseEntity.ok(batchStatus);
    }


    private Map<String, Object> buildStatus(String clientId, Instant now) {
        Instant last = lastHeartbeatMap.get(clientId);
        Instant first = firstHeartbeatMap.get(clientId);
        Duration duration = Duration.between(last, now);

        Map<String, Object> status = new ConcurrentHashMap<>();
        status.put("clientId", clientId);
        status.put("serverTime", formatInstant(now));
        status.put("lastHeartbeat", formatInstant(last));
        status.put("firstHeartbeat", first != null ? formatInstant(first) : "N/A");
        status.put("elapsedSeconds", duration.getSeconds());

        HeartbeatPayload payload = heartbeatDataMap.get(clientId);
        if (payload != null) {
            status.put("variables", payload.getVariables());
        }

        return status;
    }



    private String formatInstant(Instant instant) {
        return instant.atZone(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREAN));
    }





    @PostMapping("/heartbeat")
    public ResponseEntity<String> heartbeat(@RequestBody HeartbeatPayload payload) {
        Instant now = Instant.now();
        String clientId = payload.getClientId();

        lastHeartbeatMap.put(clientId, now);
        firstHeartbeatMap.putIfAbsent(clientId, now);
        heartbeatDataMap.put(clientId, payload);  // 변수 데이터 저장

        System.out.println("[" + clientId + "] - " + formatNowInKoreanTime() + " Heartbeat (with variables)");
        return ResponseEntity.ok("OK");
    }



    @PostMapping("/exit")
    public ResponseEntity<String> receiveCleanExit(@RequestParam String clientId) {
        lastHeartbeatMap.remove(clientId);  // 더 이상 감시하지 않음
        System.out.println("["+ clientId +"] - "+ formatNowInKoreanTime() +" 클라이언트 종료 감지");
        return ResponseEntity.ok("Client exit acknowledged");
    }

    @Scheduled(fixedRate = 5000) // 5초마다 체크
    public void checkHeartbeats() {
        Instant now = Instant.now();

        for (Map.Entry<String, Instant> entry : lastHeartbeatMap.entrySet()) {
            Duration sinceLast = Duration.between(entry.getValue(), now);
            if (sinceLast.getSeconds() > 10) {
                System.out.println("["+ entry.getKey() +"] - "+ formatNowInKoreanTime() + " Heartbeat 중단 감지됨.");
                sendToSynologyChat("["+ entry.getKey() +"] - "+ formatNowInKoreanTime() + " Heartbeat 중단 감지됨.");
                lastHeartbeatMap.remove(entry.getKey());
            }
        }
    }


    private void sendToSynologyChat(String message) {
        chatSender.sendMessage(message);

        System.out.println("[Synology Chat 전송] " + message);
    }

    public static String formatNowInKoreanTime() {
        return Instant.now()
                .atZone(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("a HH:mm:ss.SSS", Locale.KOREAN));
    }

}
