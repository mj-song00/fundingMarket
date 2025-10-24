package market.fundingmarket.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PaymentRequest {
    @NotBlank
    private String paymentKey;

    @NotBlank
    private String orderId;

    @NotBlank
    private int amount;
}
