package market.fundingmarket.domain.order.repository;

import market.fundingmarket.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository< Order, Long> {
    Optional<Order> findByOrderId(String orderId);
}
