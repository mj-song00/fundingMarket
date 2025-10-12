package market.fundingmarket.domain.project.reward.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;

@Getter
@Entity
@Table(name = "reward")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED) // 외부 직접 호출을 막기 위해 protected 설정
public class FundingReward extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long price; // 후원 가격

    @Column
    private String description; // 가격에 포함된 것
}
