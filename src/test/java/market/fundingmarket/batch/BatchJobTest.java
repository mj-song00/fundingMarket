package market.fundingmarket.batch;

import market.fundingmarket.common.config.batch.ProjectBatchConfig;
import market.fundingmarket.domain.creator.entity.Creator;
import market.fundingmarket.domain.creator.repository.CreatorRepository;
import market.fundingmarket.domain.file.repository.FileRepository;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.enums.Category;
import market.fundingmarket.domain.project.enums.FundingStatus;
import market.fundingmarket.domain.project.repository.ProjectRepository;
import market.fundingmarket.domain.reward.repository.RewardRepository;
import market.fundingmarket.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static market.fundingmarket.domain.project.enums.Category.GAME;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@Import(ProjectBatchConfig.class)
public class BatchJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    // ProjectBatchConfig에서 빈으로 등록한 Job을 주입
    @Autowired
    private Job updateFundingStatusJob;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private FileRepository fileRepository;

    private final DateTimeFormatter internalDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private final DateTimeFormatter jobParameterFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Creator testCreator;

    private static final Category DEFAULT_CATEGORY = Category.GAME;
    private static final String DEFAULT_CONTENTS = "Default project contents.";
    private static final String DEFAULT_DELIVERY_DATE = "2025.04.20";
    @Autowired
    private RewardRepository rewardRepository;

    private String createScheduleString(LocalDate endDate) {
        // 시작일은 종료일 30일 전으로 설정하여 유효한 스케줄 범위 생성
        return endDate.minusDays(30).format(internalDateFormatter) +
                " . " +
                endDate.format(internalDateFormatter);
    }


    @BeforeEach
    @Commit
    @Transactional
    void setup() {
        rewardRepository.deleteAllInBatch();
        fileRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        creatorRepository.deleteAllInBatch();
        // 테스트 Job 실행을 위해 JobLauncherTestUtils에 Job을 설정
        jobLauncherTestUtils.setJob(updateFundingStatusJob);

        // 테스트를 위한 Creator 생성 (Project 엔티티는 Creator 엔티티에 의존한다고 가정)
        testCreator = new Creator(
                "test@creator.com",
                "BatchTestCreator",
                "creator_test",
                UserRole.CREATOR
        );

        testCreator = creatorRepository.save(testCreator);

        // 1. Job 실행 기준일
        LocalDate today = LocalDate.now();

        // --- 1. 성공 케이스: 종료일 지남, 목표 달성 (COMPLETION 예상) ---
        Project successProject = new Project(
                "성공 펀딩",
                GAME,
                "DEFAULT_CONTENTS",
                100000L,
                createScheduleString(today.minusDays(1)),
                "DEFAULT_DELIVERY_DATE",
                testCreator
        );
        successProject.setCollectedAmount(150000); // *setter 필요*
        successProject.updateStatus(FundingStatus.IN_PROGRESS); // *updateStatus 필요*

        // --- 2. 실패 케이스: 종료일 지남, 목표 미달 (INTERRUPTION 예상) ---
        Project failureProject = new Project(
                "실패 펀딩",
                DEFAULT_CATEGORY,
                DEFAULT_CONTENTS,
                200000L,
                createScheduleString(today.minusDays(1)),
                DEFAULT_DELIVERY_DATE,
                testCreator
        );
        failureProject.setCollectedAmount(50000);
        failureProject.updateStatus(FundingStatus.IN_PROGRESS);


        // --- 3. 제외 케이스: 아직 진행 중 (변경되면 안 됨) ---
        Project ongoingProject = new Project(
                "진행 중 펀딩",
                DEFAULT_CATEGORY,
                DEFAULT_CONTENTS,
                50000L,
                createScheduleString(today.plusDays(10).plusDays(10)),
                DEFAULT_DELIVERY_DATE,
                testCreator
        );
        ongoingProject.setCollectedAmount(5000);
        ongoingProject.updateStatus(FundingStatus.IN_PROGRESS);

        // 4. 제외 케이스: 이미 종료됨 (변경되면 안 됨)
        Project alreadyClosedProject = new Project(
                "이미 종료된 펀딩",
                DEFAULT_CATEGORY,
                DEFAULT_CONTENTS,
                50000L,
                createScheduleString(today.minusDays(5)),
                DEFAULT_DELIVERY_DATE,
                testCreator
        );
        alreadyClosedProject.setCollectedAmount(50000);
        alreadyClosedProject.updateStatus(FundingStatus.COMPLETION);

        // DB에 테스트 데이터 저장
        projectRepository.saveAll(List.of(successProject, failureProject, ongoingProject, alreadyClosedProject));
    }

    @Test
    @DisplayName("종료일이 지난 펀딩 프로젝트의 상태가 올바르게 업데이트되는지 검증")
    void testUpdateFundingStatusJob() throws Exception {
        // given: @BeforeEach에서 테스트 데이터 준비 완료

        // JobParameter는 Reader에서 @Value("#{jobParameters['now']}")로 LocalDate를 받기 때문에
        // yyyy-MM-dd 형식의 문자열로 전달해야 합니다.
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("now", LocalDate.now().format(jobParameterFormatter)) // 👈 LocalDate를 String으로 변환
                .addLong("run.id", System.currentTimeMillis()) // 중복 실행 방지용
                .toJobParameters();

        // when: Job 실행
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then:
        // 1. Job이 성공적으로 완료되었는지 검증
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // 2. DB 상태 검증
        // 펀딩이 성공으로 업데이트되어야 할 프로젝트 조회 (목표 달성)
        List<Project> completedProjects = projectRepository.findByStatus(FundingStatus.COMPLETION);
        assertThat(completedProjects).as("목표를 달성한 프로젝트는 COMPLETION 상태여야 합니다.")
                .hasSize(2);

        // 펀딩이 실패로 업데이트되어야 할 프로젝트 조회 (목표 미달)
        List<Project> interruptedProjects = projectRepository.findByStatus(FundingStatus.INTERRUPTION);
        assertThat(interruptedProjects).as("목표에 미달한 프로젝트는 INTERRUPTION 상태여야 합니다.")
                .hasSize(1);

        // 상태가 변경되지 않고 IN_PROGRESS로 남아있어야 할 프로젝트 조회
        List<Project> inProgressProjects = projectRepository.findByStatus(FundingStatus.IN_PROGRESS);
        assertThat(inProgressProjects).as("종료일이 지나지 않은 프로젝트는 IN_PROGRESS 상태여야 합니다.")
                .hasSize(1);
    }
}