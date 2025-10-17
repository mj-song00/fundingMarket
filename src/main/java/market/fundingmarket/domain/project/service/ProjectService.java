package market.fundingmarket.domain.project.service;

import jakarta.validation.Valid;
import market.fundingmarket.domain.project.dto.request.RegistrationRequest;
import market.fundingmarket.domain.project.dto.request.UpdateFundingRequest;
import market.fundingmarket.domain.project.dto.response.ProjectResponse;
import market.fundingmarket.domain.user.dto.AuthUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {
    void register(@Valid RegistrationRequest registrationRequest, AuthUser authUser,  List<MultipartFile> images);

    void update(AuthUser authUser, UpdateFundingRequest updateRequest, Long fundingId);

    ProjectResponse getProject(Long projectId);

    void termination(AuthUser authUser, Long fundingId);
}
