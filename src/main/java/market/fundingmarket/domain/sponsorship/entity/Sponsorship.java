package market.fundingmarket.domain.sponsorship.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.reward.entity.Reward;
import market.fundingmarket.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "sponsorship")
@NoArgsConstructor
public class Sponsorship extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long amount; // 후원 금액

    @Column
    private LocalDateTime sponsoredAt; // 후원 시점

    @Column
    private int quantity; // 선택한 리워드 갯수

    @Column
    private boolean cancelled = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 후원자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // 후원한 프로젝트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward; // 선택한 리워드


    public Sponsorship (Long amount, int quantity,
                        User user, Project project, Reward reward) {
        this.amount = amount;
        this.sponsoredAt = LocalDateTime.now();
        this.quantity = quantity;
        this.user = user;
        this.project = project;
        this.reward = reward;
    }

    public void cancel() {
        this.cancelled = true;
    }
}
