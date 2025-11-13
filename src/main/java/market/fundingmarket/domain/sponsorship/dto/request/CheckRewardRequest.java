package market.fundingmarket.domain.sponsorship.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckRewardRequest {
    private Long projectId;

    private Long rewardId;

    private int quantity;

    private int amount;
}
