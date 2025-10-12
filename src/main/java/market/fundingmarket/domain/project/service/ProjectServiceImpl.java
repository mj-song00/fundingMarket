package market.fundingmarket.domain.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.project.dto.request.RegistrationRequest;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.enums.FundingStatus;
import market.fundingmarket.domain.project.repository.ProjectRepository;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.entity.User;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl  implements ProjectService{
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void register(RegistrationRequest registrationRequest, AuthUser authUser) {
        User user = getUser(authUser.getId());

        Project funding = new Project(
                registrationRequest.getTitle(),
                registrationRequest.getContents()  != null ? registrationRequest.getContents() : "",
                registrationRequest.getImage() != null ? registrationRequest.getImage() : "",
                registrationRequest.getCategory(),
                registrationRequest.getFundingAmount(),
                registrationRequest.getFundingSchedule()
        );

        user.updateCreator(UserRole.CREATOR);

        funding.updateStatus(FundingStatus.InPROGRESS);

        projectRepository.save(funding);

    }

    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

}
