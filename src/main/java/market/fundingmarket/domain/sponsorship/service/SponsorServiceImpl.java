package market.fundingmarket.domain.sponsorship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import market.fundingmarket.common.exception.BaseException;
import market.fundingmarket.common.exception.ExceptionEnum;
import market.fundingmarket.domain.file.entity.File;
import market.fundingmarket.domain.file.repository.FileRepository;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.repository.ProjectRepository;
import market.fundingmarket.domain.reward.entity.Reward;
import market.fundingmarket.domain.sponsorship.dto.response.SponsorResponse;
import market.fundingmarket.domain.sponsorship.entity.Sponsorship;
import market.fundingmarket.domain.sponsorship.repository.SponsorRepository;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.entity.User;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SponsorServiceImpl implements SponsorService {
    private final SponsorRepository sponsorRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Override
    @Transactional
    public List<SponsorResponse> getList(AuthUser authUser) {

        User user = getUser(authUser.getId());

        List<Sponsorship> sponsorships = sponsorRepository.findByUserId(user.getId());

        return sponsorships.stream()
                .map(sponsor -> {
                    Project project = sponsor.getProject();

                    // 섬네일 이미지 조회 (FileRepository 사용)  project.getImages() 제거
                    List<File> thumbnailImages = fileRepository
                            .findByProjectIdAndIsThumbnailTrue(project.getId()); //project_id 기준 조회

                    // 선택한 리워드 조회 getFundingRewards() 제거, sponsor.getReward() 사용
                    Reward selectedReward = sponsor.getReward();

                    Project selectedProject =sponsor.getProject();
                    // DTO 생성  한 번에 생성자 통합
                    return new SponsorResponse(sponsor, thumbnailImages, selectedReward, selectedProject);
                })
                .toList();
    }


    private User getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (user.getUserRole() != UserRole.USER){
            throw new BaseException(ExceptionEnum.CHECK_USER_ROLE);
        }

        return user;
    }
}
