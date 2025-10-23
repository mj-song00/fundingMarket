package market.fundingmarket.domain.reward.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.creator.repository.CreatorRepository;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.repository.ProjectRepository;
import market.fundingmarket.domain.reward.dto.request.UpdateRewardRequest;
import market.fundingmarket.domain.reward.entity.Reward;
import market.fundingmarket.domain.reward.repository.RewardRepository;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.enums.UserRole;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardService {
    private final CreatorRepository creatorRepository;
    private final ProjectRepository projectRepository;
    private final RewardRepository rewardRepository;

    public void modifyReward(AuthUser authUser, UpdateRewardRequest request, Long projectId, Long rewardId) {
        getUser(authUser.getId());

        projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.FUNDING_NOT_FOUND));

        Reward reward =  findById(rewardId);

        reward.update(request.getDescription(), request.getPrice(), request.getQuantity());

        rewardRepository.save(reward);
    }

    public void delete(AuthUser authUser, Long projectId, Long rewardId) {
        getUser(authUser.getId());

        projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.FUNDING_NOT_FOUND));

        Reward reward =  findById(rewardId);

        reward.delete();
        rewardRepository.save(reward);
    }

    private Creator getUser(UUID id) {
        Creator user = creatorRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.CREATOR_NOT_FOUND));

        if (user.getUserRole() != UserRole.CREATOR) {
            throw new BaseException(ExceptionEnum.CHECK_USER_ROLE);
        }

        return user;
    }

    private Reward findById(Long id) {
        return rewardRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.REWARD_NOT_FOUND));
    }



}
