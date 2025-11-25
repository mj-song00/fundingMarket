package market.fundingmarket.domain.payment.dto.response;

public record PaymentResponse(String paymentKey, String orderId, int amount) {
}
