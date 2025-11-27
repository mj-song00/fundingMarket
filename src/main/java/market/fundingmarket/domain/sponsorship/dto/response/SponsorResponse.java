package market.fundingmarket.domain.sponsorship.dto.response;

import lombok.Getter;
import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.file.entity.File;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.reward.entity.Reward;
import market.fundingmarket.domain.sponsorship.entity.Sponsorship;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class SponsorResponse {
    private final Long id;
    private final int amount;
    private final String sponsoredAt;
    private final int quantity;
    private final boolean isCanceled;
    private final CreatorInfo creator;
    private final RewardInfo reward;
    private final List<FileInfo> images;
    private final ProjectInfo project;


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

    @Getter
    public static class ProjectInfo {
        private final Long id;
        private final String title;
        private final String expectedDeliveryDate;

        public ProjectInfo(Project project) {
            this.id = project.getId();
            this.title = project.getTitle();
            this.expectedDeliveryDate = project.getExpectedDeliveryDate();
        }

    }

    public SponsorResponse(Sponsorship sponsor, List<File> thumbnailImage, Reward reward, Project project) {
        this.id = sponsor.getId();
        this.amount = sponsor.getAmount();
        this.sponsoredAt = sponsor.getSponsoredAt();
        this.quantity = sponsor.getQuantity();
        this.isCanceled = sponsor.isCanceled();
        this.creator = new CreatorInfo(sponsor.getProject().getCreator());
        this.reward = new RewardInfo(reward);
        this.images = thumbnailImage.stream()
                .map(FileInfo::new)
                .toList();
        this.project = new ProjectInfo(project);
    }
}
