package market.fundingmarket.domain.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "payment")
@NoArgsConstructor
public class Payment extends Timestamped {
    @Id
    private String paymentKey; //결제 키값

    private UUID userId; //결제한 유저

    private String orderName; // 결제 이름

    private String method; // 결제 수단

    private Integer price; // 결제 가격

    private String status; // 결제 처리 상태

    private String requestedAt; // 결제일

    private LocalDateTime approvedAt; // 결제 요청

    private String orderId;

    public Payment(String paymentKey, UUID userId, String orderName, String method,
                   Integer price, String status, String requestedAt, LocalDateTime approvedAt,
                   String orderId) {
        this.paymentKey = paymentKey;
        this.userId = userId;
        this.orderName = orderName;
        this.method = method;
        this.price = price;
        this.status = status;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.orderId = orderId;
    }
}
