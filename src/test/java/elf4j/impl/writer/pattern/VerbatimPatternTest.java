package elf4j.impl.writer.pattern;

import elf4j.impl.service.LogEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class VerbatimPatternTest {

    @Test
    void appendPatternTextAsIs() {
        LogEntry mockEntry = mock(LogEntry.class);
        String verbatimTextToAppend = "text";
        String inputLogText = "inputLogText";
        StringBuilder logTextBuilder = new StringBuilder(inputLogText);

        new VerbatimPattern(verbatimTextToAppend).render(mockEntry, logTextBuilder);

        verifyNoInteractions(mockEntry);
        assertEquals(inputLogText + verbatimTextToAppend, logTextBuilder.toString());
    }

    @Test
    void errorOnPatternIntendedForAnotherLogPatternType() {
        assertThrows(IllegalArgumentException.class, () -> VerbatimPattern.from("thread:name"));
    }
}