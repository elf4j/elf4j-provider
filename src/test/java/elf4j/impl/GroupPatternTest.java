package elf4j.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class GroupPatternTest {
    @Nested
    class render {
        @Mock LogPattern mockPattern;
        @Mock LogPattern mockPattern2;

        LayoutPattern layoutPatternEntry;

        @Test
        void dispatchAll() {
            layoutPatternEntry =
                    new LayoutPattern(Arrays.asList(new LogPattern[] { mockPattern2, mockPattern }));
            LogEntry logEntry = LogEntry.builder().build();
            StringBuilder stringBuilder = new StringBuilder();

            layoutPatternEntry.render(logEntry, stringBuilder);

            InOrder inOrder = inOrder(mockPattern, mockPattern2);
            then(mockPattern2).should(inOrder).render(logEntry, stringBuilder);
            then(mockPattern).should(inOrder).render(logEntry, stringBuilder);
        }
    }
}