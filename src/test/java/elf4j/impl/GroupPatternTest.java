package elf4j.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class GroupPatternTest {
    @Nested
    class render {
        @Mock LogPattern mockPattern;
        @Mock LogPattern mockPattern2;

        GroupPattern groupPattern;

        @Test
        void dispatchAll() {
            groupPattern = GroupPattern.builder().pattern(mockPattern2).pattern(mockPattern).build();
            LogEntry logEntry = LogEntry.builder().build();
            StringBuilder stringBuilder = new StringBuilder();

            groupPattern.render(logEntry, stringBuilder);

            InOrder inOrder = inOrder(mockPattern, mockPattern2);
            then(mockPattern2).should(inOrder).render(logEntry, stringBuilder);
            then(mockPattern).should(inOrder).render(logEntry, stringBuilder);
        }
    }
}