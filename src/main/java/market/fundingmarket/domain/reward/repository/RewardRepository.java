package market.fundingmarket.domain.reward.repository;

import market.fundingmarket.domain.reward.entity.FundingReward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<FundingReward, Long> {
}
