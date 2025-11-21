package market.fundingmarket.domain.payment.dto.response;

import lombok.Getter;

@Getter
public record CanceledResponse(
        String paymentKey, String orderId, int amount, String status) {
}


