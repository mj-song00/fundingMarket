package market.fundingmarket.domain.creator.service;

import jakarta.validation.Valid;
import market.fundingmarket.domain.user.dto.request.SignupRequest;

public interface CreatorService {
    void createCreator(@Valid SignupRequest signupRequest);
}
