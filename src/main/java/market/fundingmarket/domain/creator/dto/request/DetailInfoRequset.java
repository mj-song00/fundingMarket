package market.fundingmarket.domain.creator.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DetailInfoRequset {
    private String bankAccount;
    private String bank;
    private String introduce;
}
