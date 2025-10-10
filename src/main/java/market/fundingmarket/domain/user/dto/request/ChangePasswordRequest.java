package market.fundingmarket.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor // 객체 생성 시 모든 필드를 초기화해야 하는 경우에 사용
@Getter
public class ChangePasswordRequest {
    @NotBlank(message = "기존 비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$&*]).{8,20}$", message = "비밀번호는 8자 이상 20자 이하이며, 영문 대문자와 특수문자를 포함해야 합니다.")
    private String oldPassword;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$&*]).{8,20}$", message = "비밀번호는 8자 이상 20자 이하이며, 영문 대문자와 특수문자를 포함해야 합니다.")
    private String newPassword;
}
