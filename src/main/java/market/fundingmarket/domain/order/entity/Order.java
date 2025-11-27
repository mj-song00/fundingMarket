package market.fundingmarket.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.order.enums.OrderStatus;
import market.fundingmarket.domain.sponsorship.entity.Sponsorship;
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

    private String address; // 배송 주소

    @Column(nullable = false)
    private String phoneNumber;

    @Column
    private OrderStatus status;

    @Column
    private LocalDateTime canceledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsorship_id", nullable = false)
    private Sponsorship sponsor;


    public Order (User user, String address, String phoneNumber,
                  OrderStatus status, Sponsorship sponsor ) {
        this.user = user;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.sponsor = sponsor;
    }

    public void updateStatus(OrderStatus canceled) {
        this.status = canceled;
    }

    public void updateCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }
}
