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
        @Mock LogEntry stubLogEntry;

        GroupLogPattern groupLogPatternEntry;

        @Test
        void dispatchAll() {
            groupLogPatternEntry = new GroupLogPattern(Arrays.asList(mockPattern2, mockPattern));
            StringBuilder stringBuilder = new StringBuilder();

            groupLogPatternEntry.render(stubLogEntry, stringBuilder);

            InOrder inOrder = inOrder(mockPattern, mockPattern2);
            then(mockPattern2).should(inOrder).render(stubLogEntry, stringBuilder);
            then(mockPattern).should(inOrder).render(stubLogEntry, stringBuilder);
        }
    }
}