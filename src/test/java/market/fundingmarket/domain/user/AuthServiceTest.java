package market.fundingmarket.domain.user;

import market.fundingmarket.common.config.PasswordEncoder;
import market.fundingmarket.common.repository.RefreshTokenRepository;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

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
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiration", "3600"); // 1시간
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
}
