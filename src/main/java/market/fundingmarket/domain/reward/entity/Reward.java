package market.fundingmarket.domain.reward.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.project.entity.Project;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "reward")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED) // 외부 직접 호출을 막기 위해 protected 설정
public class Reward extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long price; // 후원 가격

    @Column
    private String description; // 리워드 설명

    @Column
    private Integer quantity; // 제작 수량

    @Column
    private String title; // 리워드 제목

    @Column
    private LocalDateTime deletedAt = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public  Reward(String title, Long price, String description, Project project) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.project = project;
    }


    public void update(String description, long price, int quantity) {
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
