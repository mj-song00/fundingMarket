package market.fundingmarket.domain.sponsorship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.annotation.Auth;
import market.fundingmarket.common.response.ApiResponse;
import market.fundingmarket.common.response.ApiResponseEnum;
import market.fundingmarket.domain.sponsorship.dto.request.CheckRewardRequest;
import market.fundingmarket.domain.sponsorship.dto.response.SponsorResponse;
import market.fundingmarket.domain.sponsorship.service.SponsorService;
import market.fundingmarket.domain.user.dto.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Sponsor", description = "후원 관련 API")
@RestController
@RequestMapping("/api/v1/sponsor")
@RequiredArgsConstructor
@Slf4j
public class SponsorController {
    private final SponsorService sponsorService;

    /**
     *
     * @param authUser
     * @param checkRewardRequest
     * @return void
     */
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

    // todo : 페이지네이션 적용 해야됨.  장당 10page

    /**
     *
     * @param authUser
     * @return List 타입 SponsorResponseDto, 페이지네이션 적용. 장당 10개
     */
    @Operation(summary = "후원 내역 보기 ", description = "후원 내역을 조회합니다. ")
    @GetMapping("/get-reward")
    public  ResponseEntity<ApiResponse<List<SponsorResponse>>> getList(
            @Auth AuthUser authUser
    ){
        List<SponsorResponse> result = sponsorService.getList(authUser);
        return ResponseEntity.ok(ApiResponse.successWithData(result, ApiResponseEnum.GET_SUCCESS));
    }

    @Operation(summary = "후원 취소", description = "후원을 취소합니다. 완료되거나 종료된 프로젝트에는 경우 사용할 수 없습니다.")
    @PutMapping("/delete/{sponsorId}")
    public ResponseEntity<ApiResponse<Void>> cancel (
            @Auth AuthUser authUser,
            @PathVariable Long sponsorId
    ){
        sponsorService.cancel(authUser, sponsorId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.FUNDING_CANCEL_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
