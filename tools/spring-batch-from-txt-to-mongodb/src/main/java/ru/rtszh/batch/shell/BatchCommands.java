package ru.rtszh.batch.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {

    private final Job exportPages;
    private final Job exportBook;

    private final JobLauncher jobLauncher;

    @ShellMethod(value = "startExportPages", key = "pg")
    public void startExportPages() throws Exception {
        jobLauncher.run(exportPages, new JobParametersBuilder()
                .toJobParameters());
    }

    @ShellMethod(value = "startExportBook", key = "b")
    public void startExportBook() throws Exception {
        jobLauncher.run(exportBook, new JobParametersBuilder()
                .toJobParameters());
    }

}
