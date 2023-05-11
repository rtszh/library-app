package ru.rtszh.batch.readers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import ru.rtszh.config.AppProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LinesReader implements Tasklet, StepExecutionListener {

    private final AppProperties properties;

    private final Logger logger = LoggerFactory.getLogger(LinesReader.class);

    private List<List<String>> lines;

    public LinesReader(AppProperties properties) {
        this.properties = properties;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
//        logger.info("start reading file: {}", properties.getInputFile());
        logger.info("start reading file: {}", properties.getInputFile());
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {

//        Path filePath = Paths.get(properties.getInputFile());

        try (Stream<Path> paths = Files.walk(Paths.get(properties.getInputFile()))) {
            lines = paths
                    .filter(Files::isRegularFile)
                    .sorted()
                    .map(path -> {
                        try {
                            return Files.readAllLines(path, StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());


        } catch (IOException ex) {
            System.out.format("I/O error: %s%n", ex);
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution
                .getJobExecution()
                .getExecutionContext()
                .put("lines", this.lines);
        logger.debug("Lines Reader ended.");
        return ExitStatus.COMPLETED;
    }
}
