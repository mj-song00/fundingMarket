package market.fundingmarket.domain.creator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import market.fundingmarket.common.authenticable.Authenticatable;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.user.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "creator")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Creator extends Timestamped  implements Authenticatable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    @Column
    private String email;

    @Column
    private String introduce;

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
    private String bank;

    @Column
    private LocalDateTime deletedAt;

    @Column
    @Builder.Default
    private boolean isActive = true;


    public Creator(String email, String password,
                   String nickName, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.userRole = userRole;
    }

    public void update(String bank, String bankAccount, String introduce) {
        this.bank = bank;
        this.bankAccount = bankAccount;
        this.introduce = introduce;
    }

    @Override
    public UserRole getRole() {
        return this.userRole;
    }

    @Override
    public LocalDateTime isDeleted() {
        return this.deletedAt;
    }
}
