package market.fundingmarket.domain.sponsorship.service;

import market.fundingmarket.domain.sponsorship.dto.request.CheckRewardRequest;
import market.fundingmarket.domain.sponsorship.dto.response.SponsorResponse;
import market.fundingmarket.domain.user.dto.AuthUser;

import java.util.List;

public interface SponsorService {
    void selectReward(AuthUser authUser, CheckRewardRequest checkRewardRequest);

    List<SponsorResponse> getList(AuthUser authUser);

    void cancel(AuthUser authUser, Long sponsorId);
}
