package market.fundingmarket.domain.reward.dto.request;

import lombok.Getter;

@Getter
public class UpdateRewardRequest {
    private int price;
    private int quantity;
    private String description;
}
