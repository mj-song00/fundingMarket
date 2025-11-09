package market.fundingmarket.domain.payment.repository;

import market.fundingmarket.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
