package market.fundingmarket.domain.payment.dto.response;

import lombok.Getter;

@Getter
public class PaymentResponse {
    private final String paymentKey;
    private final String orderId;
    private final int amount;

    public PaymentResponse(String paymentKey, String orderId, int amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }
}
