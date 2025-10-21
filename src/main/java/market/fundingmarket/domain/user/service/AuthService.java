package market.fundingmarket.domain.user.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.authenticable.Authenticatable;
import market.fundingmarket.common.config.PasswordEncoder;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.common.repository.RefreshTokenRepository;
import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.creator.repository.CreatorRepository;
import market.fundingmarket.domain.user.dto.request.LoginRequest;
import market.fundingmarket.domain.user.entity.User;
import market.fundingmarket.domain.user.repository.UserRepository;
import market.fundingmarket.jwt.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static market.fundingmarket.common.exception.ExceptionEnum.ALREADY_DELETED;
import static market.fundingmarket.common.exception.ExceptionEnum.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CreatorRepository creatorRepository;

    public String login(LoginRequest req) { // type = "USER" or "CREATOR"
        Authenticatable account = findAccountByEmail(req.getEmail() );
        validateUserNotDeleted(account);
        authenticateUser(account, req.getPassword());
        return generateAccessToken(account);
    }


    // 리프레시 토큰 생성
    public String generateRefreshToken(String email) {
        Authenticatable authUser = findByEmail(email);
        return jwtUtil.createRefreshToken(authUser.getId());
    }

    public Authenticatable findByEmail(String email) {
        // User 먼저 찾고, 없으면 Creator 찾기
        return userRepository.findByEmail(email)
                .map(u -> (Authenticatable) u)
                .or(() -> creatorRepository.findByEmail(email).map(c -> (Authenticatable) c))
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    }

    public void saveRefreshToken(String email, String refreshToken) {
        String key = "refresh:" + email;
        redisTemplate.opsForValue().set(key, refreshToken, 7, TimeUnit.DAYS);
    }

    // 리프레시 토큰 쿠키 설정
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge((long) 7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    //AccessToken 재발급
    @Transactional
    public String refreshAccessToken(String refreshToken, HttpServletResponse response) {
        User user = validateRefreshToken(refreshToken);
        long expiration = jwtUtil.getRemainingExpiration(refreshToken);

        try {
            refreshTokenRepository.addBlacklist(refreshToken, expiration);

            String newRefreshToken = generateRefreshToken(user.getEmail());
            saveRefreshToken(user.getEmail(), newRefreshToken);
            setRefreshTokenCookie(response, newRefreshToken);
        } catch (Exception e) {
            log.error("Redis 처리 실패", e);
        }

        return generateAccessToken(user);

    }

    @Transactional
    public void logout(String refreshToken, HttpServletResponse response) {
        User user = validateRefreshToken(refreshToken);

        try {
            //Redis에서 refreshToken 삭제
            redisTemplate.delete("refresh:" + user.getEmail());
        } catch (Exception e) {
            log.error("Redis 처리 실패", e);
        }

        // 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "") // 빈 문자열 사용
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    }

    // 사용자 탈퇴 여부 확인
    private void validateUserNotDeleted(Authenticatable authenticatable) {
        if ( authenticatable.isDeleted() != null) {
            throw new BaseException(ALREADY_DELETED);
        }
    }

    // 비밀번호 인증
    private void authenticateUser(Authenticatable authenticatable, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, authenticatable.getPassword())) {
            throw new BaseException(ExceptionEnum.EMAIL_PASSWORD_MISMATCH);
        }
    }

    // 액세스 토큰 생성
    private String generateAccessToken(Authenticatable authenticatable) {
        return jwtUtil.createToken(authenticatable.getId(), authenticatable.getRole());
    }

    private User validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshTokenRepository.isBlacklisted(refreshToken)
                || !jwtUtil.isTokenValid(refreshToken)) {
            throw new BaseException(ExceptionEnum.INVALID_REFRESH_TOKEN);
        }

        Claims claims = jwtUtil.extractClaims(refreshToken);
        UUID userId = UUID.fromString(claims.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

        String storedToken = refreshTokenRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new BaseException(ExceptionEnum.INVALID_REFRESH_TOKEN));

        if (!storedToken.equals(refreshToken)) {
            throw new BaseException(ExceptionEnum.INVALID_REFRESH_TOKEN);
        }

        return user;
    }

    private Authenticatable findAccountByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) return user.get();

        Optional<Creator> creator = creatorRepository.findByEmail(email);
        if (creator.isPresent()) return creator.get();

        throw new BaseException(USER_NOT_FOUND);
    }
}
