package market.fundingmarket.common.config.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import market.fundingmarket.domain.project.entity.Project;
import market.fundingmarket.domain.project.enums.FundingStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class ProjectBatchConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Bean
    @StepScope
    public JpaPagingItemReader<Project> fundingReader(
            @Value("#{jobParameters['now'] ?: T(java.time.LocalDate).now()}") LocalDate now
    )
     {
        return new JpaPagingItemReaderBuilder<Project>()
                .name("fundingReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .queryString("SELECT p FROM Project p WHERE p.endDate <= :now AND p.status = :status")
                .parameterValues(Map.of(
                        "now", now,
                        "status", FundingStatus.IN_PROGRESS
                ))
                .build();
    }


    @Bean
    public ItemProcessor<Project, Project> fundingProcessor() {
        return project -> {
            // 상태 결정
            if (project.getCollectedAmount() >= project.getFundingAmount()) {
                project.updateStatus(FundingStatus.COMPLETION);
            } else {
                project.updateStatus(FundingStatus.INTERRUPTION);
            }
            return project;
        };
    }

    @Bean
    public JpaItemWriter<Project> fundingWriter() {
        return new JpaItemWriterBuilder<Project>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step updateFundingStatusStep(
            JpaPagingItemReader<Project> fundingReader
    ) {
        return new StepBuilder("updateFundingStatusStep", jobRepository)
                .<Project, Project>chunk(100, transactionManager)
                .reader(fundingReader)
                .processor(fundingProcessor())
                .writer(fundingWriter())
                .build();
    }

    @Bean
    public Job updateFundingStatusJob(Step updateFundingStatusStep) {
        return new JobBuilder("updateFundingStatusJob", jobRepository)
                .start(updateFundingStatusStep)
                .build();
    }
}
