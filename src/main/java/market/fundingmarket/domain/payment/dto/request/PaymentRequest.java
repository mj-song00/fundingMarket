package market.fundingmarket.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentRequest {
    @NotBlank
    private String paymentKey;

    @NotBlank
    private String orderId;

    @NotBlank
    private int amount;

    @NotBlank
    private String address;

    @NotBlank
    private String phoneNumber;

    private long fundingId;

    private long rewardId;

    private int quantity;
}
