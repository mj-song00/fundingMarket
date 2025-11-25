package market.fundingmarket.domain.payment.dto.response;

public record CanceledResponse(
        String paymentKey, String orderId, int amount, String status) {
}


