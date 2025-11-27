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
}
