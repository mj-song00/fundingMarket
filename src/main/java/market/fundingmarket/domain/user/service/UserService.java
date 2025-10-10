package market.fundingmarket.domain.user.service;

import market.fundingmarket.domain.user.dto.request.SignupRequest;

public interface UserService {
    void createUser(SignupRequest signupRequest);
}
