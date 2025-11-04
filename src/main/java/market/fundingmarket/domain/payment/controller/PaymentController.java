package market.fundingmarket.domain.payment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/confirm")
    public ResponseEntity<String> confirmPayment(@RequestBody String jsonBody) {
        System.out.println("요청 바디 = " + jsonBody);

        try {
            // JSON 파싱
            JSONParser parser = new JSONParser();
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);

            String paymentKey = (String) requestData.get("paymentKey");
            String orderId = (String) requestData.get("orderId");
            String amount = String.valueOf(requestData.get("amount"));

            System.out.println("✅ paymentKey = " + paymentKey);
            System.out.println("✅ orderId = " + orderId);
            System.out.println("✅ amount = " + amount);

            // ✅ Authorization 헤더 생성
            String secretKey = "dGVzdF9za19lcVJHZ1lPMXI1TVJqQmVRekxQMnJRbk4yRXlhOg=="; // ← 대시보드의 Secret Key 원본
            String encodedAuth = Base64.getEncoder()
                    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));


            // ✅ 요청 JSON 구성
            JSONObject requestJson = new JSONObject();
            requestJson.put("paymentKey", paymentKey);
            requestJson.put("orderId", orderId);
            requestJson.put("amount", Integer.parseInt(amount));

            // ✅ HTTP 요청 전송
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson.toString()))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // ✅ 응답 출력
            System.out.println("💬 토스 응답 코드 = " + response.statusCode());
            System.out.println("💬 토스 응답 본문 = " + response.body());

            return ResponseEntity.status(response.statusCode()).body(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("message", e.getMessage());
            error.put("code", "SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error.toJSONString());
        }
    }
}
