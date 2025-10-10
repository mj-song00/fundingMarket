package market.fundingmarket.domain.user.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import market.fundingmarket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository  extends JpaRepository<User, UUID> {
    Optional<User> findByNickName(@NotBlank String nickname);
    Optional<User> findByEmail(@NotBlank @Email String email);

}
