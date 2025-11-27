package market.fundingmarket.domain.payment.dto.response;

public record CanceledResponse(
        String orderId, int amount, String status) {
}


