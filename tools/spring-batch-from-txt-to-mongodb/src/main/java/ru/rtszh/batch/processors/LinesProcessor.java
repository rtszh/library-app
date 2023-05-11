package ru.rtszh.batch.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LinesProcessor implements Tasklet, StepExecutionListener {

    private final int pageSize;
    private final Logger logger = LoggerFactory.getLogger(LinesProcessor.class);
    private final List<List<String>> chaptersWithPages;

    public LinesProcessor(int pageSize) {
        chaptersWithPages = new ArrayList<>();
        this.pageSize = pageSize;
    }

    private List<List<String>> lines;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {

        List<List<String>> formattedLines = formatLines();

        for (List<String> chapterWithLines: formattedLines) {
            List<String> chapterWithPages = new ArrayList<>();  // создание chapter1
            chapterWithPages.add(new String());     // создание page1 в chapter1
            int lastElementNumber = 0;

            for (String line: chapterWithLines) {
                StringBuilder curString = new StringBuilder(chapterWithPages.get(lastElementNumber));

                curString.append(line);

                chapterWithPages.remove(lastElementNumber);
                chapterWithPages.add(curString.toString());

                if (curString.length() > pageSize) {
                    lastElementNumber++;
                    chapterWithPages.add(new String());
                }
            }

            chaptersWithPages.add(chapterWithPages);
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution
                .getJobExecution()
                .getExecutionContext();
        this.lines = (List<List<String>>) executionContext.get("lines");
        logger.debug("Lines Processor initialized.");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution
                .getJobExecution()
                .getExecutionContext()
                .put("pages", this.chaptersWithPages);
        logger.debug("Lines Processor ended.");
        return ExitStatus.COMPLETED;
    }

    private List<List<String>> formatLines() {

        List<List<String>> formattedList = new ArrayList<>();

        for (List<String> chapter : lines) {
            List<String> handledLines = chapter.stream()
                    .map(line -> {
                        if (line.equals("")) {
                            return "\n";
                        } else {
                            return line;
                        }
                    })
                    .collect(Collectors.toList());

            formattedList.add(handledLines);
        }

        return formattedList;
    }
}
