package market.fundingmarket.domain.sponsorship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.repository.ProjectRepository;
import market.fundingmarket.domain.reward.entity.FundingReward;
import market.fundingmarket.domain.reward.repository.RewardRepository;
import market.fundingmarket.domain.sponsorship.dto.request.CheckRewardRequest;
import market.fundingmarket.domain.sponsorship.entity.Sponsorship;
import market.fundingmarket.domain.sponsorship.repository.SponsorRepository;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.entity.User;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.repository.UserRepository;
import market.fundingmarket.domain.user.validation.UserValidation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SponsorServiceImpl implements SponsorService {
    private final SponsorRepository sponsorRepository;
    private final UserRepository userRepository;
    private final UserValidation userValidation;
    private final ProjectRepository projectRepository;
    private final RewardRepository rewardRepository;

    @Override
    @Transactional
    public void selectReward(AuthUser authUser, CheckRewardRequest checkRewardRequest) {
        userValidation.validateAuthenticatedUser(authUser);
        User user = getUser(authUser.getId());
        validateProject(authUser, checkRewardRequest.getProjectId());

        Project project = projectRepository.findById(checkRewardRequest.getProjectId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.FUNDING_NOT_FOUND));
        FundingReward reward = rewardRepository.findById(checkRewardRequest.getRewardId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.REWARD_NOT_FOUND));

        Sponsorship sponsor = new Sponsorship(
                checkRewardRequest.getAmount(),
                checkRewardRequest.getQuantity(),
                user,
                project,
                reward
        );


        sponsorRepository.save(sponsor);
    }

    private User getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (user.getUserRole() != UserRole.USER){
            throw new BaseException(ExceptionEnum.CHECK_USER_ROLE);
        }

        return user;
    }

    private Project validateProject(AuthUser authUser, Long projectId){
        Project funding = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.FUNDING_NOT_FOUND));

        if(!funding.getCreator().getId().equals(authUser.getId())){
            throw new BaseException(ExceptionEnum.CREATOR_NOT_FOUND);
        }

        if (funding.getDeletedAt()!= null) throw new BaseException(ExceptionEnum.FUNDING_NOT_FOUND);

        return funding;
    }
}
