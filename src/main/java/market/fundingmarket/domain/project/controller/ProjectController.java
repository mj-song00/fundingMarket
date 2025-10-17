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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Project", description = "project 관련 API")
@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 등록", description = "펀딩 프로젝트를 등록합니다. 창작자만 등록이 가능합니다.")
    @PostMapping(value = "/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestPart  RegistrationRequest registrationRequest,
            @Auth AuthUser authUser,
            @RequestPart("images") List<MultipartFile> images
            ){

        projectService.register(registrationRequest, authUser, images);

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
        System.out.println(" ??="+ projectId);
            ProjectResponse result =  projectService.getProject(projectId);

            return ResponseEntity.ok(ApiResponse.successWithData(result, ApiResponseEnum.GET_SUCCESS));
    }

    @Operation(summary = "프로젝트 종료", description = "프로젝트를 종료합니다. 해당 API는 목표금액 달성 실패, 펀딩이 성공될 경우 사용됩니다.")
    @PatchMapping("/termination/{fundingId}")
    public ResponseEntity<ApiResponse<Void>> terminationFunding(
            @Auth AuthUser authUser,
            @PathVariable Long fundingId
    ){
        projectService.termination(authUser, fundingId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.UPDATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
