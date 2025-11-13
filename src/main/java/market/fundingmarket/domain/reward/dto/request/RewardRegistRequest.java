package market.fundingmarket.domain.reward.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RewardRegistRequest {
    private String title;
    private Long price;
    private String description;
}
