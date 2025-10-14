package market.fundingmarket.domain.sponsorship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.annotation.Auth;
import market.fundingmarket.common.response.ApiResponse;
import market.fundingmarket.common.response.ApiResponseEnum;
import market.fundingmarket.domain.sponsorship.dto.request.CheckRewardRequest;
import market.fundingmarket.domain.sponsorship.service.SponsorService;
import market.fundingmarket.domain.user.dto.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Sponsor", description = "후원 관련 API")
@RestController
@RequestMapping("/api/v1/sponsor")
@RequiredArgsConstructor
@Slf4j
public class SponsorController {
    private final SponsorService sponsorService;

    @Operation(summary = "reward 결정", description = "후원할 금액을 선택합니다. 하나만 선택이 가능합니다 ")
    @PostMapping("check-reward")
    public ResponseEntity<ApiResponse<Void>> sponsor(
            @Auth AuthUser authUser,
            @RequestBody CheckRewardRequest checkRewardRequest
            ){
        sponsorService.selectReward(authUser, checkRewardRequest);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.SELECT_REWARD_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
