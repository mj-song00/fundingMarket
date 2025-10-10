package market.fundingmarket.domain.user.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.config.PasswordEncoder;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.dto.request.SignupRequest;
import market.fundingmarket.domain.user.dto.response.UserProfileResponse;
import market.fundingmarket.domain.user.entity.User;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.repository.UserRepository;
import market.fundingmarket.domain.user.validation.UserValidation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidation userValidation;
    private final AuthService authService;

    @Transactional
    @Override
    public void createUser(SignupRequest signupRequest){
        Optional<User> userByEmail = userRepository.findByEmail(signupRequest.getEmail());
        Optional<User> userByNickname = userRepository. findByNickName(signupRequest.getNickName());

        if (userByEmail.isPresent()) {
            throw new BaseException(ExceptionEnum.USER_ALREADY_EXISTS);
        }

        if (userByNickname.isPresent()) {
            throw new BaseException(ExceptionEnum.USER_ALREADY_EXISTS);
        }


        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.User;

        User user = new User(
                signupRequest.getEmail(),
                signupRequest.getNickName(),
                encodedPassword,
                userRole
        );

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(AuthUser authUser, String oldPassword, String newPassword) {
        // 인증된 사용자 확인
        userValidation.validateAuthenticatedUser(authUser);

        // 인증된 사용자의 ID로 사용자 조회
        User user = userValidation.findUserById(authUser.getId());

        // 사용자 탈퇴 여부 확인
        userValidation.validateUserNotDeleted(user);

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BaseException(ExceptionEnum.PASSWORD_MISMATCH);
        }

        // 새 비밀번호가 기존 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BaseException(ExceptionEnum.PASSWORD_SAME_AS_OLD);
        }

        // 새 비밀번호로 업데이트
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(AuthUser authUser) {
        // 인증된 사용자 확인
        userValidation.validateAuthenticatedUser(authUser);

        // 인증된 사용자의 ID로 사용자 조회
        User user = userValidation.findUserById(authUser.getId());
        // 사용자 탈퇴 여부 확인
        userValidation.validateUserNotDeleted(user);

        // 사용자 정보 반환
        return UserProfileResponse.of(user);
    }

    @Transactional
    @Override
    public void changeNickName(AuthUser authUser, String newNickName) {
        // 인증된 사용자 확인
        userValidation.validateAuthenticatedUser(authUser);

        // 인증된 사용자의 ID로 사용자 조회
        User user = userValidation.findUserById(authUser.getId());

        // 사용자 탈퇴 여부 확인
        userValidation.validateUserNotDeleted(user);

        // 새로운 닉네임이 기존 닉네임과 동일한지 확인
        if (user.getNickName().equals(newNickName)) {
            throw new BaseException(ExceptionEnum.NICKNAME_SAME_AS_OLD);
        }

        // 닉네임 업데이트
        user.updateNickName(newNickName);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(AuthUser authenticatedUser, String refreshToken, HttpServletResponse response) {
        // 인증된 사용자 확인
        userValidation.validateAuthenticatedUser(authenticatedUser);

        // 인증된 사용자의 ID로 사용자 조회
        User user = userValidation.findUserById(authenticatedUser.getId());

        // 사용자 탈퇴 여부 확인
        userValidation.validateUserNotDeleted(user);

        // 사용자 소프트 삭제 처리
        user.updateDeletedAt();
        userRepository.save(user);

        // 로그아웃 처리 (리프레시 토큰 블랙리스트 및 쿠키 삭제)
        authService.logout(refreshToken, response);
    }

    @Override
    public void createCreator(SignupRequest signupRequest) {
        Optional<User> userByEmail = userRepository.findByEmail(signupRequest.getEmail());
        Optional<User> userByNickname = userRepository. findByNickName(signupRequest.getNickName());

        if (userByEmail.isPresent()) {
            throw new BaseException(ExceptionEnum.USER_ALREADY_EXISTS);
        }

        if (userByNickname.isPresent()) {
            throw new BaseException(ExceptionEnum.USER_ALREADY_EXISTS);
        }


        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.Creator;

        User user = new User(
                signupRequest.getEmail(),
                signupRequest.getNickName(),
                encodedPassword,
                userRole
        );

        userRepository.save(user);
    }
}
