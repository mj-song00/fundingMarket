package market.fundingmarket.domain.sponsorship.dto.response;

import lombok.Getter;
import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.file.entity.File;
import market.fundingmarket.domain.reward.entity.Reward;
import market.fundingmarket.domain.sponsorship.entity.Sponsorship;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class SponsorResponse {
    private final Long id;
    private final Long amount;
    private final LocalDateTime sponsoredAt;
    private final int quantity;
    private final boolean cancelled;
    private final String expectedDeliveryDate;
    private final CreatorInfo creator;
    private final RewardInfo reward;
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

    public SponsorResponse (Sponsorship sponsor, List<File> thumbnailImage, Reward reward){
        this.id = sponsor.getId();
        this.amount = sponsor.getAmount();
        this.sponsoredAt = sponsor.getSponsoredAt();
        this.quantity = sponsor.getQuantity();
        this.cancelled = sponsor.isCancelled();
        this.expectedDeliveryDate = sponsor.getProject().getExpectedDeliveryDate();
        this.creator = new CreatorInfo(sponsor.getProject().getCreator());
        this.reward = new RewardInfo(reward);
        this.images = thumbnailImage.stream()
                .map(FileInfo::new)
                .toList();
    }
}
