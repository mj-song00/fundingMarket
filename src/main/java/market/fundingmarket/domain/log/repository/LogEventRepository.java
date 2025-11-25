package market.fundingmarket.domain.log.repository;

import market.fundingmarket.domain.log.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEventRepository extends JpaRepository<EventLog, Long> {
}
