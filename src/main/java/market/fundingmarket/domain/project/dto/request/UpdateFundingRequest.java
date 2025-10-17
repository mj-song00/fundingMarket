package market.fundingmarket.domain.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import market.fundingmarket.domain.file.entity.File;
import market.fundingmarket.domain.reward.entity.FundingReward;

import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateFundingRequest {
    private String title;

    private List<File> image;

    private String contents;

    private String fundingSchedule;

    private List<FundingReward> reward;

}
