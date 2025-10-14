package market.fundingmarket.domain.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.project.dto.request.RegistrationRequest;
import market.fundingmarket.domain.project.dto.request.UpdateFundingRequest;
import market.fundingmarket.domain.project.dto.response.ProjectResponse;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.enums.FundingStatus;
import market.fundingmarket.domain.project.repository.ProjectRepository;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.entity.Creator;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.repository.CreatorRepository;
import market.fundingmarket.domain.user.validation.UserValidation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl  implements ProjectService{
    private final ProjectRepository projectRepository;
    private final UserValidation userValidation;
    private final CreatorRepository creatorRepository;

    @Override
    @Transactional
    public void register(RegistrationRequest registrationRequest, AuthUser authUser) {
        Creator user = getUser(authUser.getId());

        Project funding = new Project(
                registrationRequest.getTitle(),
                registrationRequest.getCategory(),
                registrationRequest.getContents()  != null ? registrationRequest.getContents() : "",
                registrationRequest.getFundingAmount(),
                registrationRequest.getFundingSchedule(),
                registrationRequest.getExpectedDeliveryDate(),
                registrationRequest.getFundingRewards(),
                registrationRequest.getImages(),
                user
        );

        funding.updateStatus(FundingStatus.IN_PROGRESS);

        projectRepository.save(funding);

    }

    @Override
    @Transactional
    public void update(AuthUser authUser, UpdateFundingRequest updateRequest, Long fundingId) {
        // 인증된 사용자 확인
        userValidation.validateAuthenticatedUser(authUser);

        Project project = validateProject(authUser, fundingId);

        project.update(updateRequest.getTitle(),
                updateRequest.getImage(),
                updateRequest.getContents(),
                updateRequest.getFundingSchedule(),
                updateRequest.getReward()
                );

        projectRepository.save(project);
    }

    @Override
    public ProjectResponse getProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.FUNDING_NOT_FOUND));
        return new ProjectResponse(project);
    }

    @Override
    @Transactional
    public void termination(AuthUser authUser, Long fundingId) {
        getUser(authUser.getId());
        userValidation.validateAuthenticatedUser(authUser);
        validateProject(authUser, fundingId);

        Project funding = projectRepository.findById(fundingId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.FUNDING_NOT_FOUND));

        funding.updateStatus(FundingStatus.INTERRUPTION);
        funding.updateDelete();
        projectRepository.save(funding);
    }


    private  Creator getUser(UUID id) {
        Creator user = creatorRepository.findByCreatorId(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.CREATOR_NOT_FOUND));

        if (user.getUserRole() != UserRole.CREATOR){
            throw new BaseException(ExceptionEnum.CHECK_USER_ROLE);
        }

        return user;
    }

    private Project validateProject(AuthUser authUser,  Long fundingId){
        Project funding = projectRepository.findById(fundingId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.FUNDING_NOT_FOUND));

        if(!funding.getCreator().getId().equals(authUser.getId())){
            throw new BaseException(ExceptionEnum.CREATOR_NOT_FOUND);
        }

        if (funding.getDeletedAt()!= null) throw new BaseException(ExceptionEnum.FUNDING_NOT_FOUND);

        return funding;
    }
}
