package market.fundingmarket.domain.payment.dto.response;

import lombok.Getter;

@Getter
public record PaymentResponse(String paymentKey, String orderId, int amount) {
}
