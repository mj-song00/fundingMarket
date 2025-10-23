package market.fundingmarket.domain.reward.repository;

import market.fundingmarket.domain.reward.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    List<Reward> findByProjectIdAndDeletedAtIsNull(Long projectId);
}
