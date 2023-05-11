package ru.rtszh.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.rtszh.batch.processors.LinesProcessor;
import ru.rtszh.batch.readers.LinesReader;
import ru.rtszh.batch.writers.batch_book.BookWriter;
import ru.rtszh.batch.writers.batch_pages.PagesWriter;
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
    private AppProperties appProperties;
    @Autowired
    private BookProperties bookProperties;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PageRepository pageRepository;

    @Bean
    public Job exportPages() {
        return jobBuilderFactory.get("exportPagesJob")
                .incrementer(new RunIdIncrementer())
                .start(readLines())
                .next(processLines())
                .next(writePagesToDb())
                .build();
    }

    @Bean
    public Job exportBook() {
        return jobBuilderFactory.get("exportBookJob")
                .incrementer(new RunIdIncrementer())
                .start(writeBookToDb())
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
    protected Step writePagesToDb() {
        return stepBuilderFactory.get("writePagesToDb")
                .tasklet(pagesWriter())
                .build();
    }

    @Bean
    protected Step writeBookToDb() {
        return stepBuilderFactory.get("writeBookToDb")
                .tasklet(bookWriter())
                .build();
    }

    // readers
    @Bean
    public LinesReader linesReader() {
        return new LinesReader(appProperties);
    }

    // processors
    @Bean
    public LinesProcessor linesProcessor() {
        return new LinesProcessor(appProperties.getPageSize());
    }

    // writers
    @Bean
    public PagesWriter pagesWriter() {
        return new PagesWriter(appProperties, bookRepository, pageRepository);
    }

    @Bean
    public BookWriter bookWriter() {
        return new BookWriter(bookProperties, bookRepository);
    }

}
