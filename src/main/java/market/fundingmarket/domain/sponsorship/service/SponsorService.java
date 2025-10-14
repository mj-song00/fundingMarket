package market.fundingmarket.domain.sponsorship.service;

import market.fundingmarket.domain.sponsorship.dto.request.CheckRewardRequest;
import market.fundingmarket.domain.user.dto.AuthUser;

public interface SponsorService {
    void selectReward(AuthUser authUser, CheckRewardRequest checkRewardRequest);
}
