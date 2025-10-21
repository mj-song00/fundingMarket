package market.fundingmarket.domain.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.creator.repository.CreatorRepository;
import market.fundingmarket.domain.file.entity.File;
import market.fundingmarket.domain.file.repository.FileRepository;
import market.fundingmarket.domain.file.service.FileServie;
import market.fundingmarket.domain.project.dto.request.RegistrationRequest;
import market.fundingmarket.domain.project.dto.request.UpdateFundingRequest;
import market.fundingmarket.domain.project.dto.response.ProjectResponse;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.enums.FundingStatus;
import market.fundingmarket.domain.project.repository.ProjectRepository;
import market.fundingmarket.domain.reward.entity.FundingReward;
import market.fundingmarket.domain.reward.repository.RewardRepository;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.validation.UserValidation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl  implements ProjectService{
    private final ProjectRepository projectRepository;
    private final UserValidation userValidation;
    private final CreatorRepository creatorRepository;
    private final FileServie fileService;
    private final FileRepository fileRepository;
    private final RewardRepository rewardRepository;


    @Override
    @Transactional
    public void register(RegistrationRequest registrationRequest, AuthUser authUser,
                         List<MultipartFile> images, MultipartFile thumbnail) {
        Creator user = getUser(authUser.getId());


        Project funding = new Project(
                registrationRequest.getTitle(),
                registrationRequest.getCategory(),
                registrationRequest.getContents()  != null ? registrationRequest.getContents() : "",
                registrationRequest.getFundingAmount(),
                registrationRequest.getFundingSchedule(),
                registrationRequest.getExpectedDeliveryDate(),
                user
        );

        funding.updateStatus(FundingStatus.IN_PROGRESS);

        projectRepository.save(funding);

        List<FundingReward> rewards = registrationRequest.getFundingRewards().stream()
                .map(r -> new FundingReward(r.getPrice(), r.getDescription(), funding))
                .toList();

        rewardRepository.saveAll(rewards);

        fileService.updateThumbnail(thumbnail, funding );
        fileService.saveFile(images, authUser, funding );
    }

    @Override
    @Transactional
    public void update(AuthUser authUser, UpdateFundingRequest updateRequest, Long projectId,
                       List<MultipartFile> images, MultipartFile thumbnail) {

        // 유저 확인
        getUser(authUser.getId());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.FUNDING_NOT_FOUND));

        Path uploadDir = Paths.get("upload");
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                throw new BaseException(ExceptionEnum.UPLOAD_FAILED);
            }
        }

        // 1. 글 수정
        if (updateRequest.getContent() != null) {
            project.update(updateRequest.getTitle(), updateRequest.getContent());
        }

        // 2. 썸네일 교체
        if (thumbnail != null) {
            fileService.updateThumbnail(thumbnail, project);
        }

        // 3. 기존 이미지 삭제
        if (updateRequest.getDeleteImageIds() != null && !updateRequest.getDeleteImageIds().isEmpty()) {
            List<File> toDelete = fileRepository.findAllById(updateRequest.getDeleteImageIds());
            fileRepository.deleteAll(toDelete);
        }

        // 4. 이미지 추가
        if (images != null) {
            fileService.saveFile(images, authUser, project);
        }

        // Project 저장
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public ProjectResponse getProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.FUNDING_NOT_FOUND));

        List<File> files = fileRepository.findByProjectId(projectId);
        List<FundingReward> rewards = rewardRepository.findByProjectId(projectId);
        return new ProjectResponse(project, files, rewards);
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
        Creator user = creatorRepository.findById(id)
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
