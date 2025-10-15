package market.fundingmarket.domain.creator.service;

import jakarta.validation.Valid;
import market.fundingmarket.domain.creator.dto.request.DetailInfoRequset;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.dto.request.SignupRequest;

public interface CreatorService {
    void createCreator(@Valid SignupRequest signupRequest);

    void info(AuthUser authUser, DetailInfoRequset detailInfoRequset);
}
