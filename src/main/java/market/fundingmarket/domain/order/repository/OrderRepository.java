package market.fundingmarket.domain.order.repository;

import market.fundingmarket.domain.order.entity.Order;
import market.fundingmarket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository< Order, Long> {
    @Query("""
    SELECT o FROM Order o
    JOIN FETCH o.sponsor s
    JOIN FETCH s.project
    WHERE o.user.id = :userId
""")
    List<Order> findAllWithSponsorAndProjectByUserId(@Param("userId") UUID userId);
}
