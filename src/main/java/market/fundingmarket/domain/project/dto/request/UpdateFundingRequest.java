package market.fundingmarket.domain.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import market.fundingmarket.domain.project.image.entity.Image;
import market.fundingmarket.domain.reward.entity.FundingReward;

import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateFundingRequest {
    private String title;

    private List<Image> image;

    private String contents;

    private String fundingSchedule;

    private List<FundingReward> reward;

}
