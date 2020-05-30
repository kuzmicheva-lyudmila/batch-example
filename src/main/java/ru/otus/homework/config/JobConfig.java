package ru.otus.homework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.homework.model.Book;
import ru.otus.homework.repository.BookRepository;

import javax.sql.DataSource;
import java.util.List;

import static ru.otus.homework.config.BookRowMapper.SELECT_SQL;

@Configuration
@Slf4j
public class JobConfig {

    public static final String DATA_MIGRATE_JOB_NAME = "dataMigrateJob";

    private static final int CHUNK_SIZE = 5;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @StepScope
    @Bean
    public JdbcCursorItemReader<Book> postgresReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Book>()
                .dataSource(dataSource)
                .name("bookReader")
                .sql(SELECT_SQL)
                .rowMapper(new BookRowMapper())
                .build();
    }

    @StepScope
    @Bean
    public RepositoryItemWriter<Book> mongoWriter(BookRepository bookRepository) {
        return new RepositoryItemWriterBuilder<Book>()
                .repository(bookRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Job importBookJob(Step migrateStep) {
        return jobBuilderFactory.get(DATA_MIGRATE_JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .flow(migrateStep)
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("Start job");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("End job");
                    }
                })
                .build();
    }

    @Bean
    public Step migrateStep(ItemWriter<Book> mongoWriter, ItemReader<Book> postgresReader) {
        return stepBuilderFactory.get("migrateStep")
                .<Book, Book>chunk(CHUNK_SIZE)
                .reader(postgresReader)
                .writer(mongoWriter)
                .listener(new ItemReadListener<>() {
                    public void beforeRead() { log.info("Start read"); }
                    public void afterRead(Book b) { log.info("End read"); }
                    public void onReadError(Exception e) { log.info("Error read"); }
                })
                .listener(new ItemWriteListener<>() {
                    public void beforeWrite(List list) { log.info("Start write"); }
                    public void afterWrite(List list) { log.info("End write"); }
                    public void onWriteError(Exception e, List list) { log.info("Error write"); }
                })
                .listener(new ChunkListener() {
                    public void beforeChunk(ChunkContext chunkContext) { log.info("Start chunk"); }
                    public void afterChunk(ChunkContext chunkContext) { log.info("End chunk"); }
                    public void afterChunkError(ChunkContext chunkContext) { log.info("Error chunk"); }
                })
                .allowStartIfComplete(true)
                .build();
    }
}
