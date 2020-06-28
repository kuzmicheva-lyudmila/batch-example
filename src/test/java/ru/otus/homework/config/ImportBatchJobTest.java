package ru.otus.homework.config;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.homework.model.Book;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.homework.config.JobConfig.DATA_MIGRATE_JOB_NAME;

@SpringBootTest
@SpringBatchTest
public class ImportBatchJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @SneakyThrows
    @Test
    public void testJob() {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParametersBuilder().toJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        assertThat(actualJobInstance.getJobName()).isEqualTo(DATA_MIGRATE_JOB_NAME);
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");

        assertThat(mongoTemplate.findAll(Book.class, "books")).isNotEmpty();
    }
}
