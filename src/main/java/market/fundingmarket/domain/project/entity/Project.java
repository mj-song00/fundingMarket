package market.fundingmarket.domain.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.project.enums.Category;
import market.fundingmarket.domain.project.enums.FundingStatus;
import market.fundingmarket.domain.reward.entity.FundingReward;
import market.fundingmarket.domain.creator.entity.Creator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "project")
@NoArgsConstructor
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

    @Column
    private String expectedDeliveryDate; // 배송 예상일

    @Column
    private LocalDateTime deletedAt;

    @ManyToOne
    private Creator creator;

//    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "project_id")
//    private List<FundingReward> rewards = new ArrayList<>();

    public Project (String title, Category category,
                   String contents, Long fundingAmount, String fundingSchedule, String expectedDeliveryDate,
                    Creator creator) {
        this.title = title;
        this.category = category;
        this.contents = contents;
        this.fundingAmount = fundingAmount;
        this.fundingSchedule = fundingSchedule;
        this.expectedDeliveryDate = expectedDeliveryDate;
//        this.rewards = rewards;
        this.creator = creator;
    }


    public void updateStatus(FundingStatus fundingStatus) {
        this.status = fundingStatus;
    }

    public void update(String title,  String contents,
       String fundingSchedule,   List<FundingReward> reward
    ) {
        this.title = title;
        this.contents = contents;
        this.fundingSchedule = fundingSchedule;
//        this.rewards = reward;
    }

    public void updateDelete(){
        this.deletedAt = LocalDateTime.now();
    }
}
