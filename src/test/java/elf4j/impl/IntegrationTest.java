package elf4j.impl;

import elf4j.Logger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class IntegrationTest {
    @Nested
    class defaultLogger {
        @Test
        void hey() {
            Logger logger = Logger.instance();
            logger.atInfo().log("Hello, world!");

            Exception issue = new Exception("Test ex message");
            logger.atWarn().log(issue, "Testing issue '{}' in {}", issue, this.getClass());
        }
    }
}
