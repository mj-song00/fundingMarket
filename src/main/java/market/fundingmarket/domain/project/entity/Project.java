package market.fundingmarket.domain.project.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.project.enums.Category;
import market.fundingmarket.domain.project.enums.FundingStatus;
import market.fundingmarket.domain.project.image.entity.Image;
import market.fundingmarket.domain.reward.entity.FundingReward;
import market.fundingmarket.domain.user.entity.CreatorProfile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "project")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED) // 외부 직접 호출을 막기 위해 protected 설정
public class Project extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column
    @Enumerated(EnumType.STRING)
    private Category category;

    @Lob
    @Column
    private String contents;

    @Column(nullable = false)
    private Long fundingAmount; // 펀딩 금액

    @Column(nullable = false)
    private String fundingSchedule; // 펀딩 일정 (2025. 01.01 - 2025 03.31)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FundingStatus status; // 펀딩 상태 (진행 , 중단 , 완료)

    @ManyToOne
    @JoinColumn(name = "creator_profile_id", nullable = false)
    private CreatorProfile creatorProfile;

    @OneToMany(mappedBy = "project")
    private List<FundingReward> rewards = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "project_id")
    private List<Image> image = new ArrayList<>();

    public Project(String title, Category category,
                   String contents, Long fundingAmount, String fundingSchedule,
                   List<FundingReward> rewards, List<Image> image) {
        this.title = title;
        this.category = category;
        this.contents = contents;
        this.fundingAmount = fundingAmount;
        this.fundingSchedule = fundingSchedule;
        this.rewards = rewards;
        this.image = image;
    }


    public void updateStatus(FundingStatus fundingStatus) {
        this.status = fundingStatus;
    }
}
