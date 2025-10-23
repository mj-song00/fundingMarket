package market.fundingmarket.domain.project.dto.response;

import lombok.Getter;
import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.file.entity.File;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.enums.Category;
import market.fundingmarket.domain.reward.entity.Reward;

import java.util.List;
import java.util.UUID;
@Getter
public class ProjectResponse {
    private final Long id;
    private final String title;
    private final String contents;
    private final Category category;
    private final String fundingSchedule;
    private final String expectedDeliveryDate;
    private final CreatorInfo creator;
    private final List<RewardInfo> rewards;
    private final List<FileInfo> images;

    @Getter
    public static class CreatorInfo {
        private final UUID id;
        private final String introduce;

        public CreatorInfo(Creator creator) {
            this.id = creator.getId();
            this.introduce = creator.getIntroduce();
        }
    }

    @Getter
    public static class RewardInfo {
        private final Long id;
        private final String description;
        private final Long price;

        public RewardInfo(Reward reward) {
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


    public ProjectResponse(Project project, List<File> images, List<Reward> rewards ) {
        this.id = project.getId();
        this.title = project.getTitle();
        this.contents = project.getContents();
        this.category = project.getCategory();
        this.fundingSchedule = project.getFundingSchedule();
        this.expectedDeliveryDate = project.getExpectedDeliveryDate();
        this.creator = new CreatorInfo(project.getCreator());
        this.rewards = rewards.stream().map(RewardInfo::new).toList();
        this.images = images.stream()
                .map(FileInfo::new)
                .toList();
    }

}
