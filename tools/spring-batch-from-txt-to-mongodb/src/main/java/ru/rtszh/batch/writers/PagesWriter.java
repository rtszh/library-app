package ru.rtszh.batch.writers;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import ru.rtszh.config.AppProperties;
import ru.rtszh.domain.Book;
import ru.rtszh.domain.Page;
import ru.rtszh.exceptions.BookNotFoundException;
import ru.rtszh.repository.BookRepository;
import ru.rtszh.repository.PageRepository;

import java.util.List;
import java.util.Objects;

public class PagesWriter implements Tasklet, StepExecutionListener {

    private final AppProperties properties;
    private final BookRepository bookRepository;
    private final PageRepository pageRepository;

    private List<List<String>> chaptersWithPages;

    public PagesWriter(AppProperties properties, BookRepository bookRepository, PageRepository pageRepository) {
        this.properties = properties;
        this.bookRepository = bookRepository;
        this.pageRepository = pageRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        String title = properties.getBookName();
        Book book = bookRepository.findByTitle(title);

        checkBookFounded(book, title);
        int curPage = 1;

        for (int i = 0; i < chaptersWithPages.size(); i++) {

            List<String> chapter = chaptersWithPages.get(i);

            for (int j = 0; j < chapter.size(); j++) {

                Page page = Page.builder()
                        .chapter(i + 1)
                        .pageNumber(curPage)
                        .text(chapter.get(j))
                        .bookId(book)
                        .build();

                pageRepository.save(page);

                curPage++;
            }
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution
                .getJobExecution()
                .getExecutionContext();
        this.chaptersWithPages = (List<List<String>>) executionContext.get("pages");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    private void checkBookFounded(Book book, String title) {
        if (Objects.isNull(book)) {
            throw new BookNotFoundException(
                    String.format(
                            "Book with title '%s' not found in database", title
                    )
            );
        }
    }
}
