package market.fundingmarket.domain.sponsorship.repository;

import market.fundingmarket.domain.sponsorship.entity.Sponsorship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SponsorRepository extends JpaRepository<Sponsorship, Long> {

    List<Sponsorship> findByUserId(UUID id);
}
