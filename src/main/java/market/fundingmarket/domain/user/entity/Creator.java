package market.fundingmarket.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.user.enums.UserRole;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "creator")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Creator extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    @Column
    private String email;

    @Column
    private String introduction;

    @Column
    private String password;

    @Column
    private String nickName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Column
    private String bankAccount;

    @Column
    private LocalDateTime deleteAt;

    @Column
    @ColumnDefault("true")
    private boolean isActive;


}
