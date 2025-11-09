package market.fundingmarket.domain.order.repository;

import market.fundingmarket.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository< Order, Long> {
}
