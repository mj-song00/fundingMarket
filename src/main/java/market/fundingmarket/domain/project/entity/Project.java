package market.fundingmarket.domain.project.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.project.enums.FundingStatus;
import market.fundingmarket.domain.user.entity.CreatorProfile;

import java.time.LocalDateTime;

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
    private String Category;

    @Column(nullable = false)
    private String contents;

    @Column
    private String images;

    @Column(nullable = false)
    private long fundingAmount; // 펀딩 금액

    @Column(nullable = false)
    private LocalDateTime fundingPeriod; // 펀딩 기간

    @Column(nullable = false)
    private long fundingSchedule; // 펀딩 일정

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FundingStatus status; // 펀딩 상태 (진행 , 중단 , 완료)

    @ManyToOne
    @JoinColumn(name = "creator_profile_id", nullable = false)
    private CreatorProfile creatorProfile;
}
