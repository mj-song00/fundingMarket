package market.fundingmarket.domain.creator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.annotation.Auth;
import market.fundingmarket.common.response.ApiResponse;
import market.fundingmarket.common.response.ApiResponseEnum;
import market.fundingmarket.domain.creator.dto.request.DetailInfoRequset;
import market.fundingmarket.domain.creator.service.CreatorService;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.dto.request.SignupRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Creator", description = "Creator관련 API")
@RestController
@RequestMapping("/api/v1/creator")
@RequiredArgsConstructor
@Slf4j
public class CreatorController {
    private final CreatorService creatorService;

    @Operation(summary = "크리에이터 회원가입", description = "크리에이터 전용 회원가입을 진행합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Void>> CreatorSignup(
            @Valid @RequestBody SignupRequest signupRequest) {
        creatorService.createCreator(signupRequest);
        ApiResponse<Void> response =
                ApiResponse.successWithOutData(ApiResponseEnum.SIGNUP_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "크리에이터 세부정보 등록", description = "크리에이터의 세부 정보를 등록합니다.")
    @PostMapping("/detail")
    public ResponseEntity<ApiResponse<Void>> detailInfo(
            @Auth AuthUser authUser,
            @RequestBody DetailInfoRequset detailInfoRequset
            ){
        creatorService.info(authUser, detailInfoRequset);
        ApiResponse<Void> response =
                ApiResponse.successWithOutData(ApiResponseEnum.UPDATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
