package market.fundingmarket.project;

import market.fundingmarket.domain.project.dto.request.RegistrationRequest;
import market.fundingmarket.domain.project.dto.request.UpdateFundingRequest;
import market.fundingmarket.domain.project.dto.response.ProjectResponse;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.enums.FundingStatus;
import market.fundingmarket.domain.project.image.entity.Image;
import market.fundingmarket.domain.project.repository.ProjectRepository;
import market.fundingmarket.domain.project.service.ProjectServiceImpl;
import market.fundingmarket.domain.reward.entity.FundingReward;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.entity.Creator;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.repository.CreatorRepository;
import market.fundingmarket.domain.user.validation.UserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static market.fundingmarket.domain.project.enums.Category.GAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CreatorRepository creatorRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Creator creator;
    private AuthUser authUser;

    @BeforeEach
    public void setUp(){

     creator = Creator.builder()
            .id(UUID.randomUUID())
            .email("example@test.com")
            .password("Asdf1234!")
            .nickName("tester")
            .userRole(UserRole.CREATOR)
            .introduction("테스트 소개")
            .bankAccount("123-456-789")
            .isActive(true)
            .build();

    authUser = new AuthUser(creator.getId(), creator.getEmail(), creator.getUserRole());
    }

    @Test
    @DisplayName("펀딩 프로젝트 등록")
    void registrationFundingProject() {
        //given
        Creator creatorEntity = Creator.builder()
                .id(creator.getId())
                .email(creator.getEmail())
                .password(creator.getPassword())
                .nickName(creator.getNickName())
                .userRole(creator.getUserRole())
                .introduction(creator.getIntroduction())
                .bankAccount(creator.getBankAccount())
                .isActive(true)
                .build();


        RegistrationRequest request = new RegistrationRequest(
                "테스트 프로젝트",
                GAME,
                "",
                100000L,
                "2025.01.01 - 2025.03.31",
                "2025.04.20",
                List.of(
                        new FundingReward( 10000L, "A 리워드 설명"),
                        new FundingReward(30000L, "B 리워드 설명"),
                        new FundingReward( 50000L, "C 리워드 설명")
                ),
              List.of(
                     new Image( "/url/asdf"),
                     new Image( "/urlas/sdl")
              )
        );


        when(creatorRepository.findByCreatorId(creator.getId())).thenReturn(Optional.of(creatorEntity));
        when(creatorRepository.findByCreatorId(creator.getId()))
                .thenReturn(Optional.of(creatorEntity));

        //when
        projectService.register(request, authUser);

        //than
        verify(projectRepository, times(1)).save(any(Project.class));
        assertThat(creator.getUserRole()).isEqualTo(UserRole.CREATOR);
    }

    @Test
    @DisplayName("펀딩 프로젝트 수정")
    void editProject(){
        //given
        Creator creatorEntity = Creator.builder()
                .id(creator.getId())
                .email(creator.getEmail())
                .password(creator.getPassword())
                .nickName(creator.getNickName())
                .userRole(creator.getUserRole())
                .introduction(creator.getIntroduction())
                .bankAccount(creator.getBankAccount())
                .isActive(true)
                .build();

        Project existingProject = new Project(
                "원본 제목",
                GAME,
                "원본 내용",
                100000L,
                "2025.01.01 - 2025.03.31",
                "2025.04.20",
                List.of(new FundingReward(10000L, "리워드")),
                List.of(new Image("/url/asdf")),
                creatorEntity
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));


        UpdateFundingRequest request = new UpdateFundingRequest(
                "수정1",
                List.of(
                        new Image( "asdf/pdsf.png"),
                        new Image( "asdf/pdsf.png"),
                        new Image( "asdf/pdsf.png")),
                "",
                "2025.01.01 - 2025.04.31",
                List.of()
        );

        // 호출
        projectService.update(authUser, request, 1L);

        // save된 객체 확인
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        Project savedProject = captor.getValue();

        assertThat(savedProject.getTitle()).isEqualTo("수정1");
        assertThat(savedProject.getContents()).isEqualTo("");
        assertThat(savedProject.getImage()).hasSize(3);
        assertThat(savedProject.getFundingSchedule()).isEqualTo("2025.01.01 - 2025.04.31");
    }

    @Test
    @DisplayName("펀딩 프로젝트 조회")
    void getProject() {
        // given - 기존 프로젝트 mock
        Project existingProject = new Project(
                "원본 제목",
                GAME,
                "원본 내용",
                100000L,
                "2025.01.01 - 2025.03.31",
                "2025.04.20",
                List.of(new FundingReward(10000L, "리워드")),
                List.of(new Image("/url/asdf")),
                creator
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));

        // when
        ProjectResponse result = projectService.getProject(1L);

        // then - 조회된 값 검증
        assertThat(result.getTitle()).isEqualTo("원본 제목");
        assertThat(result.getContents()).isEqualTo("원본 내용");
        assertThat(result.getImages()).hasSize(1);
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
                .introduction(creator.getIntroduction())
                .bankAccount(creator.getBankAccount())
                .isActive(true)
                .build();

        // getUser() 호출 대비 mock
        when(creatorRepository.findByCreatorId(creator.getId()))
                .thenReturn(Optional.of(creatorEntity));

        Project existingProject = new Project(
                "원본 제목",
                GAME,
                "원본 내용",
                100000L,
                "2025.01.01 - 2025.03.31",
                "2025.04.20",
                List.of(new FundingReward(10000L, "리워드")),
                List.of(new Image("/url/asdf")),
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
}
