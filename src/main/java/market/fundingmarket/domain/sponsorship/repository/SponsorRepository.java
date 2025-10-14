package market.fundingmarket.domain.sponsorship.repository;

import market.fundingmarket.domain.sponsorship.entity.Sponsorship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SponsorRepository extends JpaRepository<Sponsorship, Long> {
}
