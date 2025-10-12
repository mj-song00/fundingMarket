package market.fundingmarket.project;

import market.fundingmarket.domain.project.dto.request.RegistrationRequest;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.image.entity.Image;
import market.fundingmarket.domain.project.repository.ProjectRepository;
import market.fundingmarket.domain.project.service.ProjectServiceImpl;
import market.fundingmarket.domain.reward.entity.FundingReward;
import market.fundingmarket.domain.user.dto.AuthUser;
import market.fundingmarket.domain.user.entity.User;
import market.fundingmarket.domain.user.enums.UserRole;
import market.fundingmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static market.fundingmarket.domain.project.enums.Category.GAME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;


    @Test
    @DisplayName("펀딩 프로젝트 등록")
    void registrationFundingProject() {
        //given
        User creator = User.builder()
                .id(UUID.randomUUID())
                .email("example@test.com")
                .password("Asdf1234!")
                .nickName("tester")
                .userRole(UserRole.USER)
                .build();

        RegistrationRequest request = new RegistrationRequest(
                "테스트 프로젝트",
                GAME,
                "",
                100000L,
                "2025.01.01 - 2025.03.31",
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

        AuthUser authUser = new AuthUser(creator.getId(), creator.getEmail(), creator.getUserRole());

        when(userRepository.findById(creator.getId())).thenReturn(Optional.of(creator));

        //when
        projectService.register(request, authUser);

        //than
        verify(projectRepository, times(1)).save(any(Project.class));
        assertThat(creator.getUserRole()).isEqualTo(UserRole.CREATOR);
    }
}
