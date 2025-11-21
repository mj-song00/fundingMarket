package market.fundingmarket.domain.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.order.entity.Order;
import market.fundingmarket.domain.order.repository.OrderRepository;
import market.fundingmarket.domain.payment.dto.request.CancelRequest;
import market.fundingmarket.domain.payment.dto.request.PaymentRequest;
import market.fundingmarket.domain.payment.dto.response.CanceledResponse;
import market.fundingmarket.domain.payment.dto.response.PaymentResponse;
import market.fundingmarket.domain.payment.entity.Payment;
import market.fundingmarket.domain.payment.repository.PaymentRepository;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.repository.ProjectRepository;
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
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

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

            Project project = projectRepository.findById(paymentRequest.getFundingId())
                    .orElseThrow(() -> new BaseException(ExceptionEnum.FUNDING_NOT_FOUND));

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
                    paymentRequest.getPhoneNumber(),
                    project
            );

            orderRepository.save(order);

            return new PaymentResponse(paymentKey, orderId, price);

        } catch (Exception e) {
            log.info("결제 승인 중 오류 발생", e);
            throw new BaseException(ExceptionEnum.PAYMENT_ERROR);
        }
    }

    @Transactional
    public CanceledResponse cancel(AuthUser authUser, CancelRequest cancelRequest) {

        // 1. 본인 확인: User 조회
        User user = getUser(authUser.getId());

        // 2. 주문 조회 (Order PK 기반)
        Order order = orderRepository.findByOrderId(cancelRequest.getOrderId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.ORDER_NOT_FOUND));

        // 3. 주문이 해당 사용자 것이 맞는지 체크
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BaseException(ExceptionEnum.INVALID_USER_ACCESS);
        }

        // 4. 이미 취소된 주문인지 체크
        if ("CANCELED".equals(order.getStatus())) {
            throw new BaseException(ExceptionEnum.ALREADY_CANCELED);
        }

        // 5. 펀딩 기간 체크
        Project project = order.getProject();
        if (project.isEnded()) {   // 펀딩 종료 여부 확인하는 메서드 필요
            throw new BaseException(ExceptionEnum.REFUND_NOT_ALLOWED);
        }

        // 6. Toss 결제 취소 요청
        try {
            // Toss Secret Key
            String secretKey = ""; // Base64 Encoding 필요 (너가 테스트 중이면 Key 맞춰 넣기)

            // 요청 Body 구성
            ObjectMapper mapper = new ObjectMapper();
            String requestJson = mapper.writeValueAsString(Map.of(
                    "cancelReason", cancelRequest.getCanceledReason() != null
                            ? cancelRequest.getCanceledReason()
                            : "사용자 요청"
            ));

            // Toss API 호출
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/" + order.getPaymentKey() + "/cancel"))
                    .header("Authorization", "Basic " + secretKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode json = mapper.readTree(response.body());

            // 토스 응답에서 필요한 결과 추출
            String status = json.get("status").asText();  // e.g. CANCELED
            String canceledAtStr = json.get("canceledAt").asText();
            LocalDateTime canceledAt = OffsetDateTime.parse(canceledAtStr).toLocalDateTime();

            // 7. 주문 상태 변경
            order.updateStatus("CANCELED");
            order.updateCanceledAt(canceledAt);

            // 8. 응답 생성
            return new CanceledResponse(
                    order.getPaymentKey(),
                    order.getOrderId(),
                    order.getTotalAmount(),
                    status
            );

        } catch (Exception e) {
            log.error("Toss 환불 처리 중 오류 발생", e);
            throw new BaseException(ExceptionEnum.PAYMENT_CANCEL_FAIL);
        }
    }



    private User getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (user.getUserRole() != UserRole.USER) {
            throw new BaseException(ExceptionEnum.CHECK_USER_ROLE);
        }

        return user;
    }

}
