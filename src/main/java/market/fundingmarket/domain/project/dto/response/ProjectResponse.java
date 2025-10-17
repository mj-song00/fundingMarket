package market.fundingmarket.domain.project.dto.response;

import lombok.Getter;
import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.file.entity.File;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.enums.Category;
import market.fundingmarket.domain.reward.entity.FundingReward;

import java.util.UUID;

@Getter
public class ProjectResponse {
    private final  Long id;
    private final String title;
    private final String contents;
    private final Category category;
    private final String fundingSchedule;
    private final CreatorInfo creator;
//    private final List<RewardInfo> rewards;
//    private final List<ImageInfo> images;

    @Getter
    public static class CreatorInfo{
        private final UUID id;

        public CreatorInfo(Creator creator) {
            this.id = creator.getId();
        }
    }

    @Getter
    public static class RewardInfo {
        private final Long id;
        private final String description;
        private final Long price;

        public RewardInfo(FundingReward reward) {
            this.id = reward.getId();
            this.description = reward.getDescription();
            this.price = reward.getPrice();
        }
    }

    @Getter
    public static class FileInfo {
        private final String url;

        public FileInfo(File image) {
            this.url = image.getImageUrl();
        }
    }

    public ProjectResponse(Project project) {
        this.id = project.getId();
        this.title = project.getTitle();
        this.contents = project.getContents();
        this.category = project.getCategory();
        this.fundingSchedule = project.getFundingSchedule();
        this.creator = new CreatorInfo(project.getCreator());
//        this.rewards = project.getRewards().stream()
//                .map(RewardInfo::new)
//                .toList();
//        this.images = project.getImage().stream()
//                .map(ImageInfo::new)
//                .toList();
    }
}
