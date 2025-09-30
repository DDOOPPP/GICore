package org.gi.gICore.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModuleLogger {
    private final Logger logger;
    private final String moduleName;
    private final String prefix;
    private final File logDirectory;

    public ModuleLogger(JavaPlugin plugin, String moduleName) {
        this.logger = plugin.getLogger();
        this.moduleName = moduleName;
        this.prefix = "[" + moduleName + "] ";
        this.logDirectory = new File(plugin.getDataFolder(), "logs");

        if (!logDirectory.exists()) {
            logDirectory.mkdirs();
        }
    }
    /**
     * INFO 레벨 로그
     */
    public void info(String message) {
        log(LogLevel.INFO, message, null);
    }

    public void info(String format, Object... args) {
        info(String.format(format, args));
    }

    /**
     * WARN 레벨 로그
     */
    public void warn(String message) {
        log(LogLevel.WARN, message, null);
    }

    public void warn(String format, Object... args) {
        warn(String.format(format, args));
    }

    public void warn(String message, Throwable throwable) {
        log(LogLevel.WARN, message, throwable);
    }

    /**
     * ERROR 레벨 로그
     */
    public void error(String message) {
        log(LogLevel.ERROR, message, null);
    }

    public void error(String format, Object... args) {
        error(String.format(format, args));
    }

    public void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }

    /**
     * DEBUG 레벨 로그
     */
    public void debug(String message) {
        log(LogLevel.DEBUG, message, null);
    }

    public void debug(String format, Object... args) {
        debug(String.format(format, args));
    }

    private void log(LogLevel level, String message, Throwable throwable) {
        String formattedMessage = prefix + message;

        // 콘솔 출력
        Level bukkitLevel = level.toBukkitLevel();
        if (throwable != null) {
            logger.log(bukkitLevel, formattedMessage, throwable);
        } else {
            logger.log(bukkitLevel, formattedMessage);
        }

        // 파일 로깅 (비동기)
        if (Level.WARNING.equals(bukkitLevel) || Level.SEVERE.equals(bukkitLevel)) {
            CompletableFuture.runAsync(() -> writeToFile(level, message, throwable));
        }
    }

    /**
     * 파일에 로그 기록
     */
    private void writeToFile(LogLevel level, String message, Throwable throwable) {
        try {
            String fileName = "core-" + TimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd") + ".log";
            File logFile = new File(logDirectory, fileName);

            try (FileWriter writer = new FileWriter(logFile, true)) {
                String timestamp = TimeUtil.format(LocalDateTime.now());
                String logLine = String.format("[%s] [%s] [%s] %s%n", timestamp, level, Thread.currentThread().getName(),message);
                writer.write(logLine);

                if (throwable != null) {
                    writer.write("Exception: " + throwable.getMessage() + "\n");
                    for (StackTraceElement element : throwable.getStackTrace()) {
                        writer.write("  at " + element.toString() + "\n");
                    }
                }
            }
        } catch (IOException e) {
            logger.severe("Failed to write to log file: " + e.getMessage());
        }
    }
    public enum LogLevel {
        DEBUG(Level.FINE),
        INFO(Level.INFO),
        WARN(Level.WARNING),
        ERROR(Level.SEVERE);

        private final Level bukkitLevel;

        LogLevel(Level bukkitLevel) {
            this.bukkitLevel = bukkitLevel;
        }

        public Level toBukkitLevel() {
            return bukkitLevel;
        }
    }
}
