package market.fundingmarket.common.config;

import market.fundingmarket.domain.user.AuthUser;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.common.annotation.Auth;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(Auth.class) != null;
    }

    // AuthUser 객체를 생성하여 반환
    @Override
    public Object resolveArgument(
            @Nullable MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        // JwtFilter 에서 set 한 userId, username, userRole 값을 가져옴
        UUID id = (UUID) request.getAttribute("id");
        String username = (String) request.getAttribute("username");
        String roleStr = (String) request.getAttribute("role");
        UserRole role = UserRole.valueOf(roleStr);
        return new AuthUser(id, username, role);
    }
}
