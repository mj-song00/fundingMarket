package market.fundingmarket.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String orderId; // 토스 요청 시 사용한 주문 식별자

    @Column(nullable = false)
    private String paymentKey; // 토스 결제 키

    @Column(nullable = false)
    private String orderName; // 결제한 프로젝트명

    @Column(nullable = false)
    private int totalAmount; // 총 결제 금액

    @Column(nullable = false)
    private String method; // 결제 수단

    private String status; // APPROVE, DONE, CANCELED

    private LocalDateTime approvedAt; // 승인 일자

    private String address; // 배송 주소

    @Column(nullable = false)
    private String phoneNumber;

    public Order(User user, String orderId, String paymentKey, String orderName, int totalAmount, String method, String status, LocalDateTime approvedAt, String address, String phoneNumber) {
        this.user = user;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.totalAmount = totalAmount;
        this.method = method;
        this.status = status;
        this.approvedAt = approvedAt;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
