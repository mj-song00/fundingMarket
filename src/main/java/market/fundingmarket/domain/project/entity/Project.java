package market.fundingmarket.domain.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.project.enums.Category;
import market.fundingmarket.domain.project.enums.FundingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Entity
@Table(name = "project")
@NoArgsConstructor
@Setter
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
    private Long fundingAmount; // 펀딩 목표 금액

    @Column
    private int collectedAmount; // 현재 모금액

    @Column(nullable = false)
    private String fundingSchedule; // 펀딩 일정 (2025. 01.01 - 2025 03.31)

    @Column
    private LocalDateTime endDate; // 종료일, batch에 사용됨

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FundingStatus status; // 펀딩 상태 (진행 , 중단 , 완료)

    @Column
    private String expectedDeliveryDate; // 배송 예상일

    @Column
    private LocalDateTime deletedAt = null;

    @ManyToOne
    private Creator creator;

    public Project (String title, Category category,
                   String contents, Long fundingAmount, String fundingSchedule, String expectedDeliveryDate,
                    Creator creator) {
        this.title = title;
        this.category = category;
        this.contents = contents;
        this.fundingAmount = fundingAmount;
        this.fundingSchedule = fundingSchedule;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.creator = creator;
        this.calculateEndDate();
    }


    public void updateStatus(FundingStatus fundingStatus) {
        this.status = fundingStatus;
    }

    public void update(String title,  String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void updateDelete(){
        this.deletedAt = LocalDateTime.now();
    }

    public void calculateEndDate() {
        if (this.fundingSchedule != null && this.fundingSchedule.contains(" - ")) {
            String end = this.fundingSchedule.split(" - ")[1].trim();
            this.endDate = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        }
    }

    public void updateAmount(int addedAmount) {
        this.collectedAmount += addedAmount;
    }

    public boolean isEnded() {
        return LocalDateTime.now().isAfter(endDate);
    }
}
