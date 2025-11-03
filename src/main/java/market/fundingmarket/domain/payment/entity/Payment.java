package market.fundingmarket.domain.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;

@Getter
@Entity
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends Timestamped {
    @Id
    private String paymentKey; //결제 키값

    private Long userId; //결제한 유저

    private String orderName; // 결제 이름

    private String method; // 결제 수단

    private Integer price; // 결제 가격

    private String status; // 결제 처리 상태

    private String requestedAt; // 결제일

    private String approvedAt; // 결제 승인일
}
