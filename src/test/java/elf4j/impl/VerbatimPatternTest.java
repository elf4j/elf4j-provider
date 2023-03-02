package elf4j.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class VerbatimPatternTest {

    @Test
    void appendTextAsIs() {
        LogEntry mockEntry = mock(LogEntry.class);
        String originalEntry = "originalEntry";
        String verbatimTextToAppend = "text";
        StringBuilder stringBuilder = new StringBuilder(originalEntry);

        new VerbatimPattern(verbatimTextToAppend).render(mockEntry, stringBuilder);

        verifyNoInteractions(mockEntry);
        assertEquals(originalEntry + verbatimTextToAppend, stringBuilder.toString());
    }
}