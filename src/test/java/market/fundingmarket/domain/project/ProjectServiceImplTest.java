package market.fundingmarket.domain.project;

import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.creator.repository.CreatorRepository;
import market.fundingmarket.domain.file.entity.File;
import market.fundingmarket.domain.file.repository.FileRepository;
import market.fundingmarket.domain.file.service.FileServie;
import market.fundingmarket.domain.project.dto.request.RegistrationRequest;
import market.fundingmarket.domain.project.dto.request.UpdateFundingRequest;
import market.fundingmarket.domain.project.dto.response.MainProjectResponse;
import market.fundingmarket.domain.project.dto.response.ProjectResponse;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.enums.FundingStatus;
import market.fundingmarket.domain.project.repository.ProjectRepository;
import market.fundingmarket.domain.project.service.ProjectServiceImpl;
import market.fundingmarket.domain.reward.dto.request.RewardRegistRequest;
import market.fundingmarket.domain.reward.entity.Reward;
import market.fundingmarket.domain.reward.repository.RewardRepository;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.validation.UserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static market.fundingmarket.domain.project.enums.Category.GAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CreatorRepository creatorRepository;

    @Mock
    private RewardRepository rewardRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private UserValidation userValidation;

    @Mock
    private FileServie fileService;

    @Mock
    private FileRepository fileRepository;

    private Creator creator;
    private AuthUser authUser;

    private List<Object[]> mockRepositoryResults;
    private List<MainProjectResponse> expectedResponses;

    @BeforeEach
    public void setUp(){

     creator = Creator.builder()
            .id(UUID.randomUUID())
            .email("example@test.com")
            .password("Asdf1234!")
            .nickName("tester")
            .userRole(UserRole.CREATOR)
            .introduce("테스트 소개")
            .bankAccount("123-456-789")
            .isActive(true)
            .build();

    authUser = new AuthUser(creator.getId(), creator.getEmail(), creator.getUserRole());

        Object[] result1 = new Object[]{
                1L,                       // row[0]: id
                "Project Alpha",          // row[1]: title
                "Description 1",          // row[2]: description (가정)
                new BigDecimal("4.5"),    // row[3]: calculatedRate (가정)
                100,                      // row[4]: participantCount (가정)
                "http://example.com/thumb1.jpg" // row[5]: thumbnailUrl
        };

        Object[] result2 = new Object[]{
                2L,                       // row[0]: id
                "Project Beta",           // row[1]: title
                "Description 2",          // row[2]: description (가정)
                new BigDecimal("4.8"),    // row[3]: calculatedRate (가정)
                50,                       // row[4]: participantCount (가정)
                "http://example.com/thumb2.jpg" // row[5]: thumbnailUrl
        };

        mockRepositoryResults = Arrays.asList(result1, result2);

        // 서비스 메서드가 최종적으로 반환해야 할 예상 DTO 리스트
        expectedResponses = Arrays.asList(
                new MainProjectResponse(1L, "Project Alpha", "http://example.com/thumb1.jpg"),
                new MainProjectResponse(2L, "Project Beta", "http://example.com/thumb2.jpg")
        );
    }

    @Test
    @DisplayName("펀딩 프로젝트 등록")
    void registrationFundingProject() {
        //given
        MultipartFile thumbnail = mock(MultipartFile.class);


        List<MultipartFile> images = List.of();

        List<RewardRegistRequest> rewards = Arrays.asList(
                new RewardRegistRequest("리워드 1 타이틀", 10000L, "리워드 1 상세 설명")
        );


        RegistrationRequest request = new RegistrationRequest(
                "테스트 프로젝트",
                GAME,
                "",
                100000L,
                "2025.01.01 - 2025.03.31",
                "2025.04.20",
                rewards
        );


        when(creatorRepository.findById(creator.getId()))
                .thenReturn(Optional.of(creator));

        when(fileService.updateThumbnail(any(MultipartFile.class), any(Project.class)))
                .thenReturn("https://dummy-thumbnail-url.com");
        when(fileService.saveFile(anyList(), any(AuthUser.class), any(Project.class)))
                .thenReturn(List.of("https://dummy-image1.com", "https://dummy-image2.com"));

        // when
        projectService.register(request, authUser, images, thumbnail);

        // then
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(rewardRepository, times(1)).saveAll(anyList());

        // **오류 해결: 첫 번째 인자에도 Matcher (any(MultipartFile.class))를 사용**
        verify(fileService, times(1)).updateThumbnail(any(MultipartFile.class), any(Project.class));

        // 두 번째 검증은 이미 Matcher를 잘 사용하고 있습니다.
        verify(fileService, times(1)).saveFile(anyList(), any(AuthUser.class), any(Project.class));
    }
    @Test
    @DisplayName("펀딩 프로젝트 수정")
    void editProject(){
        //given

        // 1. 필요한 Mock 객체 생성
        MultipartFile newThumbnail = mock(MultipartFile.class);
        List<MultipartFile> newImages = List.of(mock(MultipartFile.class), mock(MultipartFile.class));

        Creator creatorEntity = Creator.builder().id(creator.getId()).userRole(UserRole.CREATOR).build(); // 간소화
        Project existingProject = new Project(
                "원본 제목", GAME, "원본 내용", 100000L,
                "2025.01.01 - 2025.03.31", "2025.04.20", creatorEntity
        );
        Long targetProjectId = 1L;


        UpdateFundingRequest request = new UpdateFundingRequest(
                "수정된 제목", // updateRequest.getTitle()
                "수정된 내용",  // updateRequest.getContent()
                List.of(1L, 2L) // updateRequest.getDeleteImageIds()

        );

        // 4. Mockito Stubbing
        // getUser() 호출 Mocking
        when(creatorRepository.findById(creator.getId())).thenReturn(Optional.of(creatorEntity));
        // projectRepository.findById() Mocking
        when(projectRepository.findById(targetProjectId)).thenReturn(Optional.of(existingProject));

        // fileRepository.findAllById() Mocking (삭제 대상 파일)
        List<File> filesToDelete = List.of(
                new File("https://new-thumbnail-url1.com", "https://new-img1.com",  existingProject,true),
                new File("https://new-img1.com", "path2", existingProject, false)
        );

        when(fileRepository.findAllById(eq(request.getDeleteImageIds()))).thenReturn(filesToDelete);
        // fileService Mocking
        when(fileService.updateThumbnail(any(MultipartFile.class), any(Project.class)))
                .thenReturn("https://new-thumbnail-url2.com");
        when(fileService.saveFile(anyList(), any(AuthUser.class), any(Project.class)))
                .thenReturn(List.of("https://new-img12.com"));


        // when
        projectService.update(authUser, request, targetProjectId, newImages, newThumbnail);

        // then
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository, times(1)).save(captor.capture());
        Project savedProject = captor.getValue();

        // 2. 글 수정 검증
        assertThat(savedProject.getTitle()).isEqualTo("수정된 제목");
        assertThat(savedProject.getContents()).isEqualTo("수정된 내용");

        // 3. 파일 관련 서비스/리포지토리 호출 검증
        // 썸네일 교체 확인 (thumbnail != null)
        verify(fileService, times(1)).updateThumbnail(eq(newThumbnail), eq(existingProject));

        // 기존 이미지 삭제 확인
        verify(fileRepository, times(1)).findAllById(request.getDeleteImageIds());
        verify(fileRepository, times(1)).deleteAll(eq(filesToDelete));

        // 이미지 추가 확인 (images != null)
        verify(fileService, times(1)).saveFile(eq(newImages), eq(authUser), eq(existingProject));

        // Optional: getUser 호출 검증
        verify(creatorRepository, times(1)).findById(authUser.getId());
    }

    @Test
    @DisplayName("펀딩 프로젝트 조회")
    void getProject() {
        // given - 기존 프로젝트 mock
        Long projectId = 1L;

        Project existingProject = new Project(
                "원본 제목",
                GAME,
                "원본 내용",
                100000L,
                "2025.01.01 - 2025.03.31",
                "2025.04.20",
                creator
        );

        List<File> mockFiles = List.of(
                new File("/images/thumb.jpg", "thumb_original.jpg", existingProject, true),
                new File("/images/image1.jpg", "image1_original.jpg", existingProject, false)
        );
        when(fileRepository.findByProjectId(projectId)).thenReturn(mockFiles);

        List<Reward> mockRewards = List.of(
                new Reward("기본 리워드", 10000L, "설명 없음", existingProject),
                new Reward("프리미엄 리워드", 50000L, "고급 설명", existingProject)
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(fileRepository.findByProjectId(projectId)).thenReturn(mockFiles);
        when(rewardRepository.findByProjectIdAndDeletedAtIsNull(projectId)).thenReturn(mockRewards);

        // when
        ProjectResponse result = projectService.getProject(1L);

        // then - 조회된 값 검증
        assertThat(result.getTitle()).isEqualTo("원본 제목");
        assertThat(result.getContents()).isEqualTo("원본 내용");
        assertThat(result.getImages()).hasSize(2);
        assertThat(result.getFundingSchedule()).isEqualTo("2025.01.01 - 2025.03.31");
    }

    @Test
    @DisplayName("펀딩 프로젝트 종료 or 실패")
    void termination(){
        //given
        Creator creatorEntity = Creator.builder()
                .id(creator.getId())
                .email(creator.getEmail())
                .password(creator.getPassword())
                .nickName(creator.getNickName())
                .userRole(creator.getUserRole())
                .introduce(creator.getIntroduce())
                .bankAccount(creator.getBankAccount())
                .isActive(true)
                .build();

        // getUser() 호출 대비 mock
        when(creatorRepository.findById(creator.getId()))
                .thenReturn(Optional.of(creatorEntity));

        doNothing().when(userValidation).validateAuthenticatedUser(authUser);

        Project existingProject = new Project(
                "원본 제목",
                GAME,
                "원본 내용",
                100000L,
                "2025.01.01 - 2025.03.31",
                "2025.04.20",
         //       List.of(new FundingReward(10000L, "리워드")),
                creatorEntity
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));

        // when
        projectService.termination(authUser, 1L);

        // then
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        Project savedProject = captor.getValue();

        // 상태 검증
        assertThat(savedProject.getStatus()).isEqualTo(FundingStatus.INTERRUPTION);
        assertThat(savedProject.getDeletedAt()).isNotNull();
    }

    // 메인페이지 조회
    @Test
    @DisplayName("메인 프로젝트 목록 조회: Repository 결과가 DTO로 올바르게 변환되어야 한다")
    void getMainProjects_ShouldReturnCorrectlyMappedDtos() {
        // given
        // projectRepository.findTopProjectsByCalculatedRate() 호출 시 목업 데이터 반환 설정
        when(projectRepository.findTopProjectsByCalculatedRate())
                .thenReturn(mockRepositoryResults);

        // when
        List<MainProjectResponse> actualResponses = projectService.getMainProjects();

        // then
        // 1. 결과가 null이 아니어야 함
        assertNotNull(actualResponses, "결과 리스트는 null이 아니어야 합니다.");

        // 2. 결과 리스트의 크기가 예상과 일치해야 함
        assertEquals(expectedResponses.size(), actualResponses.size(), "결과 리스트의 크기가 일치해야 합니다.");

        // 3. 각 DTO의 내용이 예상과 일치해야 함
        for (int i = 0; i < actualResponses.size(); i++) {
            MainProjectResponse actual = actualResponses.get(i);
            MainProjectResponse expected = expectedResponses.get(i);

            assertEquals(expected.getId(), actual.getId(), "DTO의 ID가 일치해야 합니다.");
            assertEquals(expected.getTitle(), actual.getTitle(), "DTO의 Title이 일치해야 합니다.");
            assertEquals(expected.getThumbnailUrl(), actual.getThumbnailUrl(), "DTO의 ThumbnailUrl이 일치해야 합니다.");
        }
    }

    @Test
    @DisplayName("메인 프로젝트 목록 조회: Repository가 빈 리스트를 반환할 때 빈 DTO 리스트를 반환해야 한다")
    void getMainProjects_ShouldReturnEmptyListWhenRepositoryReturnsEmpty() {
        // givn
        // projectRepository.findTopProjectsByCalculatedRate() 호출 시 빈 리스트 반환 설정
        when(projectRepository.findTopProjectsByCalculatedRate())
                .thenReturn(List.of()); // 또는 Collections.emptyList()

        // when
        List<MainProjectResponse> actualResponses = projectService.getMainProjects();

        // then
        assertNotNull(actualResponses, "결과 리스트는 null이 아니어야 합니다.");
        assertEquals(0, actualResponses.size(), "빈 리스트를 반환해야 합니다.");
    }
}
