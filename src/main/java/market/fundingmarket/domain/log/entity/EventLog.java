package market.fundingmarket.domain.log.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.log.entity.enums.FundingEventType;
import market.fundingmarket.domain.log.entity.enums.LogAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventLog extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private FundingEventType eventType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LogAction logAction = LogAction.REQUEST;

    @Column(columnDefinition = "BINARY(16)")
    private UUID userId; // 로그를 남긴 user의 id

    @Column(nullable = false)
    private Long targetId; // 펀딩, 결제 id

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column
    private String ipAddress;

    @Column
    private String userAgent;

    @Column(nullable = false)
    private Long duration;    // 실행시간(ms)

    @Column
    private LocalDateTime deletedAt;

    public EventLog(FundingEventType eventType, LogAction logAction, UUID userId, Long targetId, String payload, String ipAddress, String userAgent, Long duration) {
        this.eventType = eventType;
        this.logAction = logAction;
        this.userId = userId;
        this.targetId = targetId;
        this.payload = payload;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.duration = duration;
    }
}
