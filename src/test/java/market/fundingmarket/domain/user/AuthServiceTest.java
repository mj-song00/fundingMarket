package market.fundingmarket.domain.user;

import market.fundingmarket.common.config.PasswordEncoder;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.repository.RefreshTokenRepository;
import market.fundingmarket.domain.creator.repository.CreatorRepository;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.dto.request.LoginRequest;
import market.fundingmarket.domain.user.entity.User;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.repository.UserRepository;
import market.fundingmarket.domain.user.service.AuthService;
import market.fundingmarket.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CreatorRepository creatorRepository;

    @InjectMocks
    private AuthService authService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private JwtUtil jwtUtil;

    private User user;
    private AuthUser authUser;
    PasswordEncoder passwordEncoder;

    // 이미 가입된 가짜 유저 생성
    @BeforeEach
    public void UserSetup() {
        passwordEncoder = new PasswordEncoder();
        ReflectionTestUtils.setField(authService, "passwordEncoder", passwordEncoder);

        // JWTUtil 실제 객체 + 테스트용 secretKey 세팅
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey",
                Base64.getEncoder().encodeToString("test-secret-key-01234567890123456789012345678901".getBytes()));

        // AccessToken 만료
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", "3600");

        // RefreshToken 만료
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiration", "604800");

        jwtUtil.init();
        ReflectionTestUtils.setField(authService, "jwtUtil", jwtUtil);

        // 테스트용 유저 생성
        user = User.builder()
                .id(UUID.randomUUID())
                .email("tester@test.com")
                .password(passwordEncoder.encode("Asdf1234!"))
                .nickName("비운의 테스터")
                .userRole(UserRole.USER)
                .build();
    }

    @Test
    @DisplayName("accessToken 발급 성공")
    void success_login() {

        // given
        LoginRequest loginRequest = new LoginRequest(user.getEmail(), "Asdf1234!");

        given(userRepository.findByEmail(user.getEmail()))
                .willReturn(Optional.of(user));

        // when
        String accessToken = authService.login(loginRequest);

        // 비밀번호 matches 확인
        assertTrue(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()));

        // then
        assertNotNull(accessToken);
        assertTrue(accessToken.startsWith("Bearer "));
    }

    @Test
    @DisplayName("accessToken 발급 실패 - 잘못된 email")
    void fail_invalid_email() {
        // given
        LoginRequest loginRequest = new LoginRequest("test@test.com", "Asdf1234!");

        given(userRepository.findByEmail("test@test.com"))
                .willReturn(Optional.empty());

        given(creatorRepository.findByEmail("test@test.com"))
                .willReturn(Optional.empty());
        //when
        BaseException exception = assertThrows(BaseException.class, () ->
                authService.login(loginRequest)
        );

        //then
        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("accessToken 발급 실패 - 잘못된 password")
    void fail_invalid_password() {
        // given
        LoginRequest loginRequest = new LoginRequest(user.getEmail(), "asdf1234!");

        given(userRepository.findByEmail(user.getEmail()))
                .willReturn(Optional.of(user));

        //when
        BaseException exception = assertThrows(BaseException.class, () ->
                authService.login(loginRequest)
        );

        assertFalse(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()));

        //then
        assertEquals("이메일 혹은 비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("RefreshToken 발급 성공")
    void success_refreshToken() {
        LoginRequest loginRequest = new LoginRequest(user.getEmail(), "Asdf1234!");

        given(userRepository.findByEmail(user.getEmail()))
                .willReturn(Optional.of(user));

        // when
        String refreshToken = authService.generateRefreshToken(loginRequest.getEmail());

        assertNotNull(refreshToken);
        assertTrue(refreshToken.startsWith("ey"));
    }
}
