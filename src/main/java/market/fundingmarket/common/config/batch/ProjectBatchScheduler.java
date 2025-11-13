package market.fundingmarket.common.config.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectBatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job updateFundingStatusJob;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void runFundingBatch() {
        try {
            log.info("펀딩 상태 업데이트 배치 시작: {}", LocalDateTime.now());
            JobParameters params = new JobParametersBuilder()
                    .addString("time", LocalDateTime.now().toString())
                    .toJobParameters();

            jobLauncher.run(updateFundingStatusJob, params);
            log.info("펀딩 상태 업데이트 배치 완료");
        } catch (Exception e) {
            log.error("펀딩 배치 실행 중 오류 발생", e);
        }
    }
}
