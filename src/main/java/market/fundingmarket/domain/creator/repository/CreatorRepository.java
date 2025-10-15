package market.fundingmarket.domain.creator.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import market.fundingmarket.domain.creator.entity.Creator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CreatorRepository extends JpaRepository<Creator, UUID> {

    Optional<Creator> findById(UUID id);

    Optional<Creator> findByEmail(@Email(message = "이메일 형식이 올바르지 않습니다.") @NotBlank(message = "이메일을 입력해주세요.") String email);

    Optional<Creator> findByNickName(@NotBlank(message = "닉네임을 입력해주세요.") String nickName);
}
