package market.fundingmarket.user;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import market.fundingmarket.common.config.PasswordEncoder;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.dto.response.UserProfileResponse;
import market.fundingmarket.domain.user.entity.User;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.repository.UserRepository;
import market.fundingmarket.domain.user.service.UserServiceImpl;
import market.fundingmarket.domain.user.service.AuthService;
import market.fundingmarket.domain.user.validation.UserValidation;
import market.fundingmarket.domain.user.dto.request.SignupRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserValidation userValidation;

    @Mock
    private AuthService authService;

    @Nested
    @DisplayName("회원가입")
    class CreateUser {
        private static ValidatorFactory factory;
        private static Validator validator;

        @BeforeAll
        public static void init() {
            factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }

        @Test
        @DisplayName("회원가입 - 성공")
        void createUserSuccess() {

            //given
            SignupRequest signupRequest = SignupRequest.builder()
                    .email("test@test.com")
                    .password("Asdf1234!")
                    .nickName("tester")
                    .build();

            when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(Optional.empty()); // 가입된 이메일 없음
            when(userRepository.findByNickName(signupRequest.getNickName())).thenReturn(Optional.empty());// 가입된 닉네임 없음
            when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword"); // 비밀번호 암호화

            //when
            userService.createUser(signupRequest);

            //than
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("회원가입 실패 - 중복된 email")
        void createUserFailureEmailAlreadyExists() {
            //given
            SignupRequest signupRequest = SignupRequest.builder()
                    .email("test@example.com")
                    .password("password123!A")
                    .nickName("TestUser")
                    .build();

            User existingUser = mock(User.class);

            when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(Optional.of(existingUser));

            // When & Then
            BaseException exception = assertThrows(BaseException.class, () -> {
                userService.createUser(signupRequest);
            });

            assertEquals(ExceptionEnum.USER_ALREADY_EXISTS, exception.getExceptionEnum());
        }

        @Test
        @DisplayName("회원가입 실패 - 비밀번호 규칙 불일치: 특수문자없음")
        void createUserFailDidNotMeetThePasswordRuleNoSpecialCharacters() {
            //given
            SignupRequest signupRequest = SignupRequest.builder()
                    .email("test@test.com")
                    .password("Asdf1234")
                    .nickName("tester")
                    .build();

            //when
            Set<ConstraintViolation<SignupRequest>> violations = validator.validate(signupRequest);
            //than
            for (ConstraintViolation<SignupRequest> violation : violations) {
                System.out.println(violation.getMessage());
            }
        }

        @Test
        @DisplayName("회원가입 실패 - 비밀번호 규칙 불일치: 대문자 없음")
        void createUserFailDidNotMeetThePasswordRuleWithNoCapitalLetters() {
            //given
            SignupRequest signupRequest = SignupRequest.builder()
                    .email("test@test.com")
                    .password("sdf1234!")
                    .nickName("tester")
                    .build();

            //when
            Set<ConstraintViolation<SignupRequest>> violations = validator.validate(signupRequest);

            //than
            for (ConstraintViolation<SignupRequest> violation : violations) {
                System.out.println(violation.getMessage());
            }
        }

        @Test
        @DisplayName("회원가입 실패 - 비밀번호 규칙 불일치: 8글자 미만")
        void createUserFailDidNotMeetThePasswordRuleLessThan8Characters() {
            //given
            SignupRequest signupRequest = SignupRequest.builder()
                    .email("test@test.com")
                    .password("Asd234!")
                    .nickName("tester")
                    .build();

            //when
            Set<ConstraintViolation<SignupRequest>> violations = validator.validate(signupRequest);

            //than
            for (ConstraintViolation<SignupRequest> violation : violations) {
                System.out.println(violation.getMessage());
            }
        }

        @Test
        @DisplayName("회원가입 실패 - 닉네임 중복 ")
        void createUserFailDuplicateNickname() {
            //given
            SignupRequest signupRequest = SignupRequest.builder()
                    .email("test@test.com")
                    .password("Asd234!")
                    .nickName("tester")
                    .build();

            //when
            User existingUser = mock(User.class);

            when(userRepository.findByNickName(signupRequest.getNickName())).thenReturn(Optional.of(existingUser));
            BaseException exception = assertThrows(BaseException.class, () -> {
                userService.createUser(signupRequest);
            });

            //than
            assertEquals(ExceptionEnum.USER_ALREADY_EXISTS, exception.getExceptionEnum());
        }
    }

    @Nested
    @DisplayName("비밀번호 변경")
    class password{
        @Test
        @DisplayName("비밀번호 변경 - 성공")
        void changePasswordSuccess(){
            //given
            UUID userId = UUID.randomUUID();
            AuthUser authUser = new AuthUser(userId, "test@test.com", UserRole.USER);

            User user = new User("test@test.com","tester","encodedOldPassword",UserRole.USER);
            String oldPassword = "oldPassword123!";
            String newPassword = "newPassword456!";

            when(userValidation.findUserById(userId)).thenReturn(user);
            when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
            when(passwordEncoder.matches(newPassword, user.getPassword())).thenReturn(false);
            when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

            //when
            userService.changePassword(authUser, oldPassword, newPassword);

            //than
            verify(userRepository, times(1)).save(user);
            assertEquals("encodedNewPassword", user.getPassword());
        }

        @Test
        @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
        void changePasswordFailureIncorrectOldPassword() {
            // Given
            UUID userId = UUID.randomUUID();

            AuthUser authUser = new AuthUser(userId, "test@test.com", UserRole.USER);

            User user = new User("test@test.com","tester","encodedOldPassword",UserRole.USER);

            String oldPassword = "wrongOldPassword";
            String newPassword = "newPassword456!";

            when(userValidation.findUserById(userId)).thenReturn(user);
            when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(false);

            // When & Then
            BaseException exception = assertThrows(BaseException.class, () -> {
                userService.changePassword(authUser, oldPassword, newPassword);
            });

            assertEquals(ExceptionEnum.PASSWORD_MISMATCH, exception.getExceptionEnum());
        }
    }

    @Nested
    @DisplayName("사용자 조회")
    class userInfo{
        @Test
        @DisplayName("사용자 프로필 조회 - 성공")
        void getUserProfileSuccess(){
            //given
            UUID userId = UUID.randomUUID();
            AuthUser authUser = new AuthUser(userId, "test@test.com", UserRole.USER);

            User user = new User("test@test.com","tester","encodedPassword",UserRole.USER);

            when(userValidation.findUserById(userId)).thenReturn(user);

            //when
            UserProfileResponse response = userService.getUserProfile(authUser);

            //than
            assertNotNull(response);
            assertEquals(user.getId(), response.getId());
            assertEquals(user.getEmail(), response.getEmail());
            assertEquals(user.getNickName(), response.getNickName());
        }

        @Test
        @DisplayName("사용자 프로필 조회 실패 - 인증되지 않은 사용자")
        void getUserProfileFailureUnauthenticatedUser(){
            //given
            AuthUser authUser = null;

            doThrow(new BaseException(ExceptionEnum.UNAUTHORIZED_USER))
                    .when(userValidation).validateAuthenticatedUser(authUser);

            //when
            BaseException exception = assertThrows(BaseException.class, () -> {
                userService.getUserProfile(authUser);
            });

            //than
            assertEquals(ExceptionEnum.UNAUTHORIZED_USER, exception.getExceptionEnum());
        }
    }

    @Nested
    @DisplayName("닉네임")
    class nickname{
        @Test
        @DisplayName("닉네임 변경 - 성공")
        void changeNickNameSuccess() {
            // Given
            UUID userId = UUID.randomUUID();

            AuthUser authUser = new AuthUser(userId, "test@test.com", UserRole.USER);

            User user = new User("test@test.com","tester","encodedOldPassword",UserRole.USER);


            String newNickName = "NewNickName";

            when(userValidation.findUserById(userId)).thenReturn(user);

            // When
            userService.changeNickName(authUser, newNickName);

            // Then
            verify(userRepository, times(1)).save(user);
            assertEquals(newNickName, user.getNickName());
        }

        @Test
        @DisplayName("닉네임 변경 실패 - 새로운 닉네임이 기존과 동일")
        void changeNickNameFailureSameAsOldNickName() {
            // Given
            UUID userId = UUID.randomUUID();


            AuthUser authUser = new AuthUser(userId, "test@test.com", UserRole.USER);

            User user = new User("test@test.com","tester","encodedOldPassword",UserRole.USER);
            String sameNickName = "tester";

            when(userValidation.findUserById(userId)).thenReturn(user);

            // When & Then
            BaseException exception = assertThrows(BaseException.class, () -> {
                userService.changeNickName(authUser, sameNickName);
            });

            assertEquals(ExceptionEnum.NICKNAME_SAME_AS_OLD, exception.getExceptionEnum());
        }
    }

    @Nested
    @DisplayName("회원탈퇴")
    class secession{
        @Test
        @DisplayName("회원탈퇴 - 성공")
        void deleteUserSuccess() {
            // Given
            UUID userId = UUID.randomUUID();

            AuthUser authUser = new AuthUser(userId, "test@test.com", UserRole.USER);

            User user = new User("test@test.com", "tester", "encodedPassword", UserRole.USER);

            String refreshToken = "sampleRefreshToken";
            HttpServletResponse response = mock(HttpServletResponse.class);

            when(userValidation.findUserById(userId)).thenReturn(user);

            // When
            userService.deleteUser(authUser, refreshToken, response);

            // Then
            verify(userRepository, times(1)).save(user);
            assertNotNull(user.getDeletedAt());
            verify(authService, times(1)).logout(refreshToken, response);
        }

        @Test
        @DisplayName("회원탈퇴 실패 - 이미 탈퇴한 사용자")
        void deleteUserFailureAlreadyDeletedUser() {
            // Given
            UUID userId = UUID.randomUUID();

            AuthUser authUser = new AuthUser(userId, "test@test.com", UserRole.USER);

            User user = new User("test@test.com", "tester", "encodedPassword", UserRole.USER);


            user.updateDeletedAt(); // 이미 탈퇴한 사용자로 설정

            String refreshToken = "sampleRefreshToken";
            HttpServletResponse response = mock(HttpServletResponse.class);

            when(userValidation.findUserById(userId)).thenReturn(user);
            doThrow(new BaseException(ExceptionEnum.ALREADY_DELETED))
                    .when(userValidation).validateUserNotDeleted(user);

            // When & Then
            BaseException exception = assertThrows(BaseException.class, () -> {
                userService.deleteUser(authUser, refreshToken, response);
            });

            assertEquals(ExceptionEnum.ALREADY_DELETED, exception.getExceptionEnum());
        }
    }

}
