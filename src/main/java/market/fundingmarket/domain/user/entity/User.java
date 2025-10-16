package market.fundingmarket.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import market.fundingmarket.common.authenticable.Authenticatable;
import market.fundingmarket.common.entity.Timestamped;
import market.fundingmarket.domain.user.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED) // 외부 직접 호출을 막기 위해 protected 설정
@Builder
public class User extends Timestamped implements Authenticatable  {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email; // 이메일 (불변 필드로 유지)

    @Column(nullable = false)
    private String nickName; // 닉네임

    @Column
    private String password; // 일반 로그인 사용자의 비밀번호 (소셜 로그인 사용자는 null 가능)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole; // 사용자 역할

    @Column
    private LocalDateTime deletedAt;

    public User(
            @NotBlank @Email String email,
            @NotBlank String nickName,
            String encodedPassword,
            UserRole userRole
    ){
        this.email = email;
        this.nickName = nickName;
        this.password = encodedPassword;
        this.userRole = userRole;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    // 닉네임 변경
    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    // 회원 탈퇴
    public void updateDeletedAt() {
        this.deletedAt = LocalDateTime.now();
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
