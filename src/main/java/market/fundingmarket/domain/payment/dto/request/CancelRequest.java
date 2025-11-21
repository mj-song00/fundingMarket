package market.fundingmarket.domain.payment.dto.request;

import lombok.Getter;

@Getter
public class CancelRequest {
    private String paymentKey;
    private String canceledReason;
    private String orderId;
}
