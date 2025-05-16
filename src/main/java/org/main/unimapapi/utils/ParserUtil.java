package org.main.unimapapi.utils;

import lombok.experimental.UtilityClass;
import org.main.unimapapi.configs.AppConfig;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class ParserUtil {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ServerLogger Logger = new ServerLogger();

    // for debug start after 5 seconds once
    public void startParsingScheduleDebug() {
        scheduler.scheduleAtFixedRate(
                () -> runPythonParserTask(),
                5,
                1,
                TimeUnit.HOURS
        );
        ServerLogger.logServer(ServerLogger.Level.INFO,
                String.format("Scheduled parsing started. Next run: %s", LocalDateTime.now().plusSeconds(5)));
    }

    public void startParsingSchedule() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.with(LocalTime.of(4, 0));

        if (now.toLocalTime().isAfter(LocalTime.of(4, 0))) {
            nextRun = nextRun.plusDays(1);
        }

        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();

        scheduler.scheduleAtFixedRate(
                () -> runPythonParserTask(),
                initialDelay,
                TimeUnit.DAYS.toSeconds(1),
                TimeUnit.SECONDS
        );

        ServerLogger.logServer(ServerLogger.Level.INFO,
                String.format("Scheduled parsing started. Next run: %s", nextRun));
    }

    private void runPythonParserTask() {
        try {
            runPythonScript();
            ServerLogger.logServer(ServerLogger.Level.INFO,
                    String.format("Parsing completed: %s", LocalDateTime.now()));
        } catch (Exception e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR,
                    String.format("Error during parsing task execution: %s", e));
        }
    }

    private void runPythonScript() {
        String pythonFilePath = AppConfig.getPARTHER_FILE();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonFilePath);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            ServerLogger.logServer(ServerLogger.Level.INFO,
                    String.format("Python script finished with code: %d", exitCode));

        } catch (IOException | InterruptedException e) {
            ServerLogger.logServer(ServerLogger.Level.ERROR,
                    String.format("Error when launching Python script: %s", e));
            Thread.currentThread().interrupt();
        }
    }
}