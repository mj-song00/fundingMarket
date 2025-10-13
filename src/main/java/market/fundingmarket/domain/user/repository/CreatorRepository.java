package market.fundingmarket.domain.user.repository;

import market.fundingmarket.domain.user.entity.Creator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreatorRepository extends JpaRepository<Creator, UUID> {

}
