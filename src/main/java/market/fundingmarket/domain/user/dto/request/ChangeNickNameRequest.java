package market.fundingmarket.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangeNickNameRequest {
    @NotBlank(message = "변경할 닉네임을 입력해주세요.")
    private String newNickName;
}
