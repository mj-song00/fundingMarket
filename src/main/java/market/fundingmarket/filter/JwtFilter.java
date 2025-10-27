package market.fundingmarket.filter;


import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.jwt.JwtUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtFilter  implements Filter {
    private final JwtUtil jwtUtil;
    private static final List<String> SWAGGER_WHITELIST = List.of(
            "/swagger-ui", "/swagger-ui.html", "/v3/api-docs",
            "/api-docs", "/v3/api-docs/swagger-config", "/swagger-ui/**"
    );


    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // 초기화 로직이 필요한 경우 구현
//    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = httpRequest.getRequestURI();


        // /api/ 아닌 요청은 패스
        if (!path.startsWith("/api/")) {
            chain.doFilter(httpRequest, httpResponse);
            return;
        }

        /**
         * 회원가입, 로그인 api는 jwt 토큰 불필요
         * 페이지 상세조회 jwt 불필요
         * todo 카테고리, 제목 검색 포함
         */
        if (path.startsWith("/api/v1/users/auth/sign-up") || path.startsWith("/api/v1/users/auth/sign-in")
                || path.startsWith("/api/v1/creator/sign-up") ||path.matches("^/api/v1/project/\\d+/?(\\?.*)?$")
                || path.startsWith("/api/v1/project/category")
                ||SWAGGER_WHITELIST.stream().anyMatch(path::startsWith)
        ) {
            chain.doFilter(httpRequest, httpResponse);
            return;
        }

        if (path.startsWith("/images/") || path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/webjars/")) {
            chain.doFilter(request, response);
            chain.doFilter(httpRequest, httpResponse);
            return;
        }

        // Authorization 헤더 추출
        String authorizationHeader = httpRequest.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            if (httpRequest.getCookies() != null) {
                for (Cookie cookie : httpRequest.getCookies()) {
                    if ("refreshToken".equals(cookie.getName())) {
                        authorizationHeader = "Bearer " + cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 필요합니다.");
            return;
        }

        // 헤더에서 JWT 추출
        String token = jwtUtil.getJwtFromHeader(httpRequest);
        try {
            Claims claims = jwtUtil.extractClaims(token);

            if (!jwtUtil.validateToken(token)) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 유효하지 않습니다.");
                return;
            }

            // request attribute에 저장
            httpRequest.setAttribute("id", UUID.fromString(claims.getSubject()));
            httpRequest.setAttribute("username", claims.get("username", String.class));
            httpRequest.setAttribute("role", claims.get("role", String.class));

            chain.doFilter(httpRequest, httpResponse);
        } catch (Exception e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰 검증 중 오류가 발생했습니다.");
        }
    }
}
