package market.fundingmarket.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import market.fundingmarket.domain.project.enums.Category;
import market.fundingmarket.domain.project.image.entity.Image;
import market.fundingmarket.domain.reward.entity.FundingReward;

import java.util.List;

@Getter
@AllArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "제목을 입력해 주세요")
    private String title;

    @NotBlank(message = "카테고리를 입력해주세요.")
    private Category category;

    private String contents;

    private Long fundingAmount;

    private String fundingSchedule;

    private List<FundingReward> fundingRewards;

    private List<Image> images;
}
