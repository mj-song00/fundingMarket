package market.fundingmarket.domain.user.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.dto.request.SignupRequest;
import market.fundingmarket.domain.user.dto.response.UserProfileResponse;

public interface UserService {
    void createUser(SignupRequest signupRequest);

    void changePassword(AuthUser authUser, String oldPassword, String newPassword);

    UserProfileResponse getUserProfile(AuthUser authUser);

    void changeNickName(AuthUser authUser, @NotBlank(message = "변경할 닉네임을 입력해주세요.") String newNickName);

    void deleteUser(AuthUser authenticatedUser, String refreshToken, HttpServletResponse response);
}
