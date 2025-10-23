package market.fundingmarket.domain.reward.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.annotation.Auth;
import market.fundingmarket.common.response.ApiResponse;
import market.fundingmarket.common.response.ApiResponseEnum;
import market.fundingmarket.domain.reward.dto.request.UpdateRewardRequest;
import market.fundingmarket.domain.reward.service.RewardService;
import market.fundingmarket.domain.user.dto.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reward", description = "reward 관련 API")
@RestController
@RequestMapping("/api/v1/reward")
@RequiredArgsConstructor
@Slf4j
public class RewardController {
    private final RewardService rewardService;

    @Operation(summary = "리워드 수정", description = "해당 프로젝트의 리워드를 수정합니다. 창작자만 수정이 가능합니다.")
    @PutMapping("/{projectId}/reward/{rewardId}")
    public ResponseEntity<ApiResponse<Void>> modifyReward(
            @Auth AuthUser authUser,
            @RequestBody UpdateRewardRequest request,
            @PathVariable Long projectId,
            @PathVariable Long rewardId
    ){
        rewardService.modifyReward(authUser, request, projectId, rewardId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.UPDATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "리워드 삭제", description = "해당 프로젝트의 리워드를 삭제합니다. 창작자만 삭제가 가능합니다.")
    @DeleteMapping("/{projectId}/reward/{rewardId}")
    public ResponseEntity<ApiResponse<Void>> deleteReward(
            @Auth AuthUser authUser,
            @PathVariable Long projectId,
            @PathVariable Long rewardId){
        rewardService.delete(authUser, projectId, rewardId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.UPDATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
