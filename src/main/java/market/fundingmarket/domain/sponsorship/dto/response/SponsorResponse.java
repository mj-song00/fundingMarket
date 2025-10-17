package market.fundingmarket.domain.sponsorship.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import market.fundingmarket.domain.creator.entity.Creator;

@Getter
@AllArgsConstructor
public class SponsorResponse {
    private Long projectId;
//    private Image image;
    private String title;
    private Creator creator;
//    private List<FundingReward> rewards;
    private String expectedDeliveryDate;

}
