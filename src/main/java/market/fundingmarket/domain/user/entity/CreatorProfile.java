package market.fundingmarket.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.entity.Timestamped;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "creator")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED) // 외부 직접 호출을 막기 위해 protected 설정
public class CreatorProfile extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    @Column
    private String introduction;

    @Column
    private String bankAccount;

    @Column
    private LocalDateTime deleteAt;

    @Column
    @ColumnDefault("true")
    private boolean isActive;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
