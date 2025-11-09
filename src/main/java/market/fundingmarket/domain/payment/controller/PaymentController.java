package market.fundingmarket.domain.payment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.annotation.Auth;
import market.fundingmarket.domain.payment.dto.request.PaymentRequest;
import market.fundingmarket.domain.payment.dto.response.PaymentResponse;
import market.fundingmarket.domain.payment.service.PaymentService;
import market.fundingmarket.domain.user.dto.AuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @RequestMapping(value = "/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @RequestBody PaymentRequest paymentRequest,
            @Auth AuthUser authUser ) {

        PaymentResponse response = paymentService.confirmPayment(paymentRequest, authUser);
        return ResponseEntity.ok(response);
    }
}
