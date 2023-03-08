package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class GroupLogPattern implements LogPattern {
    List<LogPattern> logPatternEntries;

    public static GroupLogPattern from(@NonNull String pattern) {
        List<LogPattern> logPatterns = new ArrayList<>();
        int length = pattern.length();
        int i = 0;
        while (i < length) {
            String iPattern;
            char character = pattern.charAt(i);
            int iEnd = pattern.indexOf('}', i);
            if (character == '{' && iEnd != -1) {
                iPattern = pattern.substring(i + 1, iEnd);
                i = iEnd + 1;
            } else {
                if (iEnd == -1) {
                    iEnd = length;
                } else {
                    iEnd = pattern.indexOf('{', i);
                    if (iEnd == -1) {
                        iEnd = length;
                    }
                }
                iPattern = pattern.substring(i, iEnd);
                i = iEnd;
            }
            logPatterns.add(LogPatternType.getLogPattern(iPattern));
        }
        return new GroupLogPattern(logPatterns);
    }

    public static void main(String[] args) {
        System.out.println("22222222222222222222222222222222222222222 " + from(
                "{timestamp:yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ} {level:5} [{thread:name}] {class:simple}#{method}: {message}"));
    }

    @Override
    public boolean includeCallerDetail() {
        return logPatternEntries.stream().anyMatch(LogPattern::includeCallerDetail);
    }

    @Override
    public boolean includeCallerThread() {
        return logPatternEntries.stream().anyMatch(LogPattern::includeCallerThread);
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder logText) {
        for (LogPattern pattern : logPatternEntries) {
            pattern.render(logEntry, logText);
        }
    }
}
