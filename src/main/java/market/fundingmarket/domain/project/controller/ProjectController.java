package market.fundingmarket.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.annotation.Auth;
import market.fundingmarket.common.response.ApiResponse;
import market.fundingmarket.common.response.ApiResponseEnum;
import market.fundingmarket.domain.project.dto.request.RegistrationRequest;
import market.fundingmarket.domain.project.dto.request.UpdateFundingRequest;
import market.fundingmarket.domain.project.dto.response.ProjectResponse;
import market.fundingmarket.domain.project.service.ProjectService;
import market.fundingmarket.domain.user.dto.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Project", description = "project 관련 API")
@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 등록", description = "펀딩 프로젝트를 등록합니다. 창작자만 등록이 가능합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegistrationRequest registrationRequest,
            @Auth AuthUser authUser
            ){
        projectService.register(registrationRequest, authUser);

        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.REGISTRATION_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "프로젝트 수정", description = "펀딩 프로젝트를 수정합니다.")
    @PutMapping("/edit/{fundingId}")
    public ResponseEntity<ApiResponse<Void>> updateFunding(
            @Auth AuthUser authUser,
            @RequestBody UpdateFundingRequest updateRequest,
            @PathVariable Long fundingId
    ){
        projectService.update(authUser, updateRequest, fundingId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.UPDATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "프로젝트 상세 조회", description = "펀딩 프로젝트를 조회합니다.")
    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @PathVariable Long projectId
    ){

            ProjectResponse result =  projectService.getProject(projectId);

            return ResponseEntity.ok(ApiResponse.successWithData(result, ApiResponseEnum.GET_SUCCESS));
    }
}
