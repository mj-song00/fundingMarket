package market.fundingmarket.domain.sponsorship.service;

import market.fundingmarket.domain.sponsorship.dto.response.SponsorResponse;
import market.fundingmarket.domain.user.dto.AuthUser;

import java.util.List;

public interface SponsorService {

    List<SponsorResponse> getList(AuthUser authUser);
}
