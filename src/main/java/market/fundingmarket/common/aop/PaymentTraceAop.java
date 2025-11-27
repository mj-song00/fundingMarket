package market.fundingmarket.common.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.domain.log.entity.EventLog;
import market.fundingmarket.domain.log.entity.enums.FundingEventType;
import market.fundingmarket.domain.log.entity.enums.LogAction;
import market.fundingmarket.domain.log.repository.LogEventRepository;
import market.fundingmarket.domain.payment.dto.request.PaymentRequest;
import market.fundingmarket.domain.payment.dto.response.CanceledResponse;
import market.fundingmarket.domain.payment.dto.response.PaymentResponse;
import market.fundingmarket.domain.user.dto.AuthUser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PaymentTraceAop {

    private final LogEventRepository logEventRepository;
    private final HttpServletRequest request;

    @Pointcut("execution(* market.fundingmarket.domain.payment.service.PaymentService.confirmPayment(..))")
    public void paymentPointcut() {
    }

    @Pointcut("execution(* market.fundingmarket.domain.payment.service.PaymentService.cancel(..))")
    public void paymentCancelPointcut() {
    }

    @Around("paymentPointcut() || paymentCancelPointcut()")
    public Object logPaymentEvent(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        Object result;
        long duration;

        try {
            result = joinPoint.proceed();
            duration = Math.max(1, System.currentTimeMillis() - start);
        } catch (Exception e) {
            duration = Math.max(1, System.currentTimeMillis() - start);
            throw e;
        }

        Object[] args = joinPoint.getArgs();

        // args 타입 캐스팅
        PaymentRequest requestDto;
        AuthUser authUser;
        try {
            requestDto = (PaymentRequest) args[0];
            authUser = (AuthUser) args[1];
        } catch (ClassCastException e) {
            log.warn("PaymentTraceAop: args 타입 불일치, 로그 저장 생략", e);
            return result;
        }

        Long fundingId = requestDto.getFundingId();
        UUID userId = authUser.getId();

        String safeIp = request != null ? getIpAddress() : null;
        String safeUserAgent = request != null ? request.getHeader("User-Agent") : null;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();

        FundingEventType eventType = methodName.equals("confirmPayment") ?
                FundingEventType.FUNDING_PAYMENT :
                FundingEventType.FUNDING_PAYMENT_CANCEL;

        // --- 요청 로그 ---
        EventLog requestLog = new EventLog(
                eventType,
                LogAction.REQUEST,
                userId,
                fundingId,
                null,  // 요청 시 payload 없음
                safeIp,
                safeUserAgent,
                duration
        );
        logEventRepository.save(requestLog);

        try {
            String payload = null;

            // --- 토스페이먼트 결과 payload ---
            if (result instanceof PaymentResponse payment) {
                payload = toJsonSafe(payment);
            } else if (result instanceof CanceledResponse canceled) {
                payload = toJsonSafe(canceled);
            }

            // --- 성공 로그 ---
            EventLog successLog = new EventLog(
                    eventType,
                    LogAction.SUCCESS,
                    userId,
                    fundingId,
                    payload,
                    safeIp,
                    safeUserAgent,
                    duration
            );
            logEventRepository.save(successLog);

            return result;

        } catch (Exception e) {
            duration = Math.max(1, System.currentTimeMillis() - start);

            // --- 실패 로그 ---
            EventLog failLog = new EventLog(
                    eventType,
                    LogAction.FAIL,
                    userId,
                    fundingId,
                    null,
                    safeIp,
                    safeUserAgent,
                    duration
            );
            logEventRepository.save(failLog);

            throw e;
        }
    }

    // ===== 유틸 메서드 =====
    private String toJsonSafe(Object obj) {
        try {
            return obj == null ? "" : new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("JSON 변환 실패", e);
            return "";
        }
    }

    private String getIpAddress() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
