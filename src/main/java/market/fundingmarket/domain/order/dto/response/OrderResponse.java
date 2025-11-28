package market.fundingmarket.domain.order.dto.response;

import lombok.Getter;
import market.fundingmarket.domain.order.entity.Order;
import market.fundingmarket.domain.order.enums.OrderStatus;
import market.fundingmarket.domain.payment.entity.Payment;

import java.time.LocalDateTime;

@Getter
public class OrderResponse {

    private final Long id;
    private final String title;
    private final String paymentMethod;
    private final LocalDateTime orderDate;
    private final String address;
    private final String isCanceled;



    public OrderResponse(Order order, Payment payment){
        this.id = order.getId();
        this.title = order.getSponsor().getProject().getTitle();
        this.paymentMethod = payment != null ? payment.getMethod() : null;
        this.orderDate = payment != null ? payment.getApprovedAt() : null;
        this.address = order.getAddress();
        this.isCanceled = order.getSponsor().getStatus();
    }
}
