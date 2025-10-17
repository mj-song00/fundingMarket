package market.fundingmarket.domain.reward.repository;

import market.fundingmarket.domain.reward.entity.FundingReward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRepository extends JpaRepository<FundingReward, Long> {
    List<FundingReward> findByProjectId(Long projectId);
}
