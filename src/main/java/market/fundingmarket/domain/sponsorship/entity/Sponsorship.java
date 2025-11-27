package market.fundingmarket.domain.sponsorship.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.reward.entity.Reward;
import market.fundingmarket.domain.user.entity.User;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "sponsorship")
@NoArgsConstructor
public class Sponsorship extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int amount; // 후원 금액

    @Column
    private String sponsoredAt; // 후원 시점

    @Column
    private int quantity = 1 ; // 선택한 리워드 갯수

    @Column
    private boolean isCanceled = false;

    @Column(nullable = false)
    private String orderId; // 토스 요청 시 사용한 주문 식별자

    @Column(nullable = false)
    private String paymentKey; // 토스 결제 키

    @Column(nullable = false)
    private String orderName; // 선택한 리워드명

    @Column(nullable = false)
    private String method; // 결제 수단

    private String status;

    private LocalDateTime approvedAt; // 승인 일자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 후원자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // 후원한 프로젝트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward; // 선택한 리워드

    public Sponsorship(int amount, String sponsoredAt, int quantity,
                       boolean isCanceled, String orderId, String paymentKey,
                       String orderName, String method, String status,
                       LocalDateTime approvedAt, User user, Project project, Reward reward) {
        this.amount = amount;
        this.sponsoredAt = sponsoredAt;
        this.quantity = quantity;
        this.isCanceled = isCanceled;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.method = method;
        this.status = status;
        this.approvedAt = approvedAt;
        this.user = user;
        this.project = project;
        this.reward = reward;
    }


    public void cancel() {
        this.isCanceled = true;
    }

    public void updateStatus() {
        this.status = "canceled";
    }
}
