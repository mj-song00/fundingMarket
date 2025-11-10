package market.fundingmarket.domain.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.order.entity.Order;
import market.fundingmarket.domain.order.repository.OrderRepository;
import market.fundingmarket.domain.payment.dto.request.PaymentRequest;
import market.fundingmarket.domain.payment.dto.response.PaymentResponse;
import market.fundingmarket.domain.payment.entity.Payment;
import market.fundingmarket.domain.payment.repository.PaymentRepository;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.entity.User;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentResponse confirmPayment(PaymentRequest paymentRequest, AuthUser authUser) {

            User user = getUser(authUser.getId());

            try {

            // 시크릿키 설정
            String secretKey = ""; // test용 공개 키

            // 요청 Object 구성
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(paymentRequest);

            // HTTP 요청 전송
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization", "Basic " + secretKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // ✅ Toss 응답 파싱
            JsonNode json = objectMapper.readTree(response.body());

            // 필요한 데이터만 추출
            String paymentKey = json.get("paymentKey").asText();
            String orderName = json.get("orderName").asText();
            String method = json.has("method") ? json.get("method").asText() : "UNKNOWN";
            int price = json.get("totalAmount").asInt();
            String status = json.get("status").asText();
            String requestedAt = json.get("requestedAt").asText();
            String approvedAtStr = json.get("approvedAt").asText();
            LocalDateTime approvedAt = OffsetDateTime.parse(approvedAtStr).toLocalDateTime();
            String orderId = json.get("orderId").asText();

            // 엔티티 저장
            Payment payment = new Payment(
                    paymentKey,
                    authUser.getId(),
                    orderName,
                    method,
                    price,
                    status,
                    requestedAt,
                    approvedAt,
                    orderId
            );
            paymentRepository.save(payment);


            Order order = new Order(
                    user,
                    payment.getOrderId(),
                    payment.getPaymentKey(),
                    payment.getOrderName(),
                    payment.getPrice(),
                    payment.getMethod(),
                    payment.getStatus(),
                    payment.getApprovedAt(),
                    paymentRequest.getAddress(),
                    paymentRequest.getPhoneNumber()
                    );

            orderRepository.save(order);

            return new PaymentResponse(paymentKey, orderId, price);

        } catch (Exception e) {
            log.info("결제 승인 중 오류 발생", e);
            throw new BaseException(ExceptionEnum.PAYMENT_ERROR);
        }
    }

    private User getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (user.getUserRole() != UserRole.USER){
            throw new BaseException(ExceptionEnum.CHECK_USER_ROLE);
        }

        return user;
    }

}
