/*
 * MIT License
 *
 * Copyright (c) 2023 Qingtian Wang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 *
 */
@Value
public class ThreadPattern implements LogPattern {
    @NonNull ThreadPattern.DisplayOption threadDisplayOption;

    /**
     * @param pattern text pattern to convert
     * @return the thread pattern converted from the specified text
     */
    @Nonnull
    public static ThreadPattern from(@NonNull String pattern) {
        if (!LogPatternType.THREAD.isTargetTypeOf(pattern)) {
            throw new IllegalArgumentException("pattern: " + pattern);
        }
        return new ThreadPattern(LogPattern.getPatternOption(pattern)
                .map(displayOption -> DisplayOption.valueOf(displayOption.toUpperCase()))
                .orElse(DisplayOption.NAME));
    }

    @Override
    public boolean includeCallerDetail() {
        return false;
    }

    @Override
    public boolean includeCallerThread() {
        return true;
    }

    @Override
    public void render(LogEntry logEntry, StringBuilder logTextBuilder) {
        LogEntry.ThreadInformation callerThread = Objects.requireNonNull(logEntry.getCallerThread());
        logTextBuilder.append(threadDisplayOption == DisplayOption.ID ? callerThread.getId() : callerThread.getName());
    }

    enum DisplayOption {
        ID,
        NAME
    }
}
