package market.fundingmarket.domain.reward.dto.request;

import lombok.Getter;

@Getter
public class RewardRegistRequest {
    private String title;
    private Long price;
    private String description;
}
