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
import market.fundingmarket.domain.project.dto.response.ProjectListResponse;
import market.fundingmarket.domain.project.dto.response.ProjectResponse;
import market.fundingmarket.domain.project.enums.Category;
import market.fundingmarket.domain.project.service.ProjectService;
import market.fundingmarket.domain.user.dto.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            @RequestPart("images") List<MultipartFile> images,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
            ){

        projectService.register(registrationRequest, authUser, images, thumbnail);

        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.REGISTRATION_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "프로젝트 수정", description = "펀딩 프로젝트를 수정합니다.")
    @PutMapping(value = "/edit/{fundingId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateFunding(
            @Auth AuthUser authUser,
            @RequestPart UpdateFundingRequest updateRequest,
            @PathVariable Long fundingId,
            @RequestPart("images") List<MultipartFile> images,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ){
        projectService.update(authUser, updateRequest, fundingId, images, thumbnail);
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

    @Operation(summary = "프로젝트 종료", description = "프로젝트를 종료합니다. 해당 API는 직접 펀딩 종료시에만 사용됩니다.")
    @PatchMapping("/termination/{fundingId}")
    public ResponseEntity<ApiResponse<Void>> terminationFunding(
            @Auth AuthUser authUser,
            @PathVariable Long fundingId
    ){
        projectService.termination(authUser, fundingId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.UPDATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @Operation(summary ="프로젝트 카테고리 조회", description = "프로젝트에서 사용 가능한 카테고리 목록을 조회합니다.")
    @GetMapping("/category")
    public  List<Map<String, String>> getCategories() {
        return Arrays.stream(Category.values())
                .map(c -> Map.of(
                        "key", c.name(),          // 서버에서 사용할 값
                        "value", c.getMessage() // 클라이언트에게 보여줄 한글
                ))
                .toList();
    }

    @Operation(summary = "카테고리별 프로젝트 조회", description = "특정 카테고리의 프로젝트 목록을 조회합니다.")
    @GetMapping("/category/{categoryKey}")
    public List<ProjectListResponse> getProjectsByCategory(@PathVariable String categoryKey) {
        Category categoryEnum = Category.valueOf(categoryKey.toUpperCase());
        return projectService.findByCategory(categoryEnum);
    }
}
