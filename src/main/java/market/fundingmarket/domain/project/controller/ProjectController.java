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
import market.fundingmarket.domain.project.service.ProjectService;
import market.fundingmarket.domain.user.dto.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Project", description = "project 관련 API")
@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 등록", description = "펀딩 프로젝트를 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegistrationRequest registrationRequest,
            @Auth AuthUser authUser
            ){
        projectService.register(registrationRequest, authUser);

        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.REGISTRATION_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
