package ru.rtszh.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.rtszh.batch.processors.LinesProcessor;
import ru.rtszh.batch.readers.LinesReader;
import ru.rtszh.batch.writers.PagesWriter;
import ru.rtszh.domain.Page;
import ru.rtszh.repository.BookRepository;
import ru.rtszh.repository.PageRepository;

@Configuration
@EnableBatchProcessing
public class JobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private AppProperties properties;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PageRepository pageRepository;

    @Bean
    public Job migrateBooks() {
        return jobBuilderFactory.get("migrateBooksJob")
                .incrementer(new RunIdIncrementer())
                .start(readLines())
                .next(processLines())
                .next(writeDataToDb())
                .build();
    }

    // steps
    @Bean
    protected Step readLines() {
        return stepBuilderFactory.get("readLines")
                .tasklet(linesReader())
                .build();
    }

    @Bean
    protected Step processLines() {
        return stepBuilderFactory.get("processLines")
                .tasklet(linesProcessor())
                .build();
    }

    @Bean
    protected Step writeDataToDb() {
        return stepBuilderFactory.get("writeDataToDb")
                .tasklet(pagesWriter())
                .build();
    }

    // readers
    @Bean
    public LinesReader linesReader() {
        return new LinesReader(properties);
    }

    // processors
    @Bean
    public LinesProcessor linesProcessor() {
        return new LinesProcessor(properties.getPageSize());
    }

    // writers
    @Bean
    public PagesWriter pagesWriter() {
        return new PagesWriter(properties, bookRepository, pageRepository);
    }

    @Bean
    public MongoItemWriter<Page> bookWriter() {
        return new MongoItemWriterBuilder<Page>()
                .collection("book1")
                .template(mongoTemplate)
                .build();
    }
}
